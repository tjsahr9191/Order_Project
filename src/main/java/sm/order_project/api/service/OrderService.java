package sm.order_project.api.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sm.order_project.api.common.DateTimeHolder;
import sm.order_project.api.controller.OrderController;
import sm.order_project.api.domain.*;
import sm.order_project.api.dto.CreateOrderDto;
import sm.order_project.api.dto.CreateOrderRequest;
import sm.order_project.api.dto.OrderStatisticsDto;
import sm.order_project.api.dto.request.OrderSearchCondition;
import sm.order_project.api.dto.response.OrderDetailResponse;
import sm.order_project.api.dto.response.SimpleOrderResponse;
import sm.order_project.api.kakao.KakaoPayConfig;
import sm.order_project.api.kakao.KakaoPayReadyRequest;
import sm.order_project.api.kakao.KakaoPayReadyResponse;
import sm.order_project.api.kakao.KakaoPayService;
import sm.order_project.api.repository.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static sm.order_project.api.kakao.KakaoPayConfig.ONE_TIME_CID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderQueryRepository orderQueryRepository;
    private final OrderStatsRepository orderStatsRepository;
    private final KakaoPayService kakaoPayService;
    private final KakaoPayConfig kakaoPayConfig;
    private final MemberRepository memberRepository;
    private final DateTimeHolder dateTimeHolder;
    private final ProductRepository productRepository;
    private final OrderDetailRepository orderDetailRepository;


    public OrderDetailResponse findOrderDetails(Long id) {

        Order foundOrder = orderRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Order not found"));

        return OrderDetailResponse.of(foundOrder);
    }

    public Page<SimpleOrderResponse> findOrdersByCondition(OrderSearchCondition condition, Pageable pageable) {

        Page<Order> orderPage = orderRepository.searchOrders(condition, pageable);

        return orderPage.map(SimpleOrderResponse::of);
    }

    public Page<OrderStatisticsDto> getOrderStatistics(Long minAmount, Pageable pageable) {
        Page<OrderStats> orderStats = orderStatsRepository.findByTotalAmountGreaterThanEqual(minAmount != null ? minAmount : 0L, pageable);

        return orderStats.map(OrderStatisticsDto::of);
    }

    public Page<OrderStatisticsDto> getOrderStatisticsv1(Long minAmount, Pageable pageable) {
        if (minAmount == null) {
            minAmount = 0L;
        }

        return orderQueryRepository.getOrderStatisticsQuerydsl(minAmount, pageable);
    }

    //    @Scheduled(cron = "0 */3 * * * *")
    @SchedulerLock(name = "ScheduledTask_run")
    @Transactional
    public void refreshOrderStatistics() {
        log.info("Starting order statistics refresh at {}", LocalDateTime.now());
        try {
            // DB에서 직접 통계 데이터 집계 및 저장
            orderStatsRepository.refreshOrderStats();
            log.info("Order statistics refreshed successfully");
        } catch (Exception e) {
            log.error("Failed to refresh order statistics", e);
            throw e;
        }
    }

    public Page<SimpleOrderResponse> search(Pageable pageable, OrderController.Condition condition) {
        Page<Order> pagedOrders = orderQueryRepository.findAllPaged(condition, pageable);

        List<SimpleOrderResponse> response = pagedOrders.getContent().stream().map(SimpleOrderResponse::of) // GetOrderDto -> GetOrderHttp.Response
                .toList();

        return new PageImpl<>(response, pageable, pagedOrders.getTotalElements());
    }

    public KakaoPayReadyResponse ready(@Valid CreateOrderRequest request, String orderNo, Long memberId) {
        return kakaoPayService.ready(createKakaoReadyRequest(orderNo, request, memberId));
    }

    private KakaoPayReadyRequest createKakaoReadyRequest(String orderNo, CreateOrderRequest request, Long memberId) {

        String cid = ONE_TIME_CID;
        String approvalUrl = kakaoPayConfig.createApprovalUrl("/payment");
        String failUrl = kakaoPayConfig.getRedirectFailUrl();
        String cancelUrl = kakaoPayConfig.getRedirectCancelUrl();

        return request.toKakaoReadyRequest(orderNo, memberId, cid, approvalUrl, failUrl, cancelUrl);
    }

    @Transactional
    public void create(CreateOrderDto createOrderDto) {

        // 1. Order 생성
        Order order = orderRepository.save(createOrder(createOrderDto));

        // 2. OrderDetail 생성 (연관관계 매핑 여기서 해결)
        List<OrderDetail> orderDetails = createOrderDetails(createOrderDto.getProductValues(), order);

        orderDetailRepository.saveAll(orderDetails);

        // 4. Product 의 stock 감소
        order.stockDecrease();
    }

    // Product 조회 로직을 수정한 createOrderDetails 메소드 (예시)
    private List<OrderDetail> createOrderDetails(List<CreateOrderDto.ProductDto> productValues, Order order) {
        List<Long> productIds = productValues.stream().map(CreateOrderDto.ProductDto::getProductId).collect(Collectors.toList());

        List<Product> products = productRepository.findAllById(productIds);

        //========================================================================================================
        // ★★★ Optimistic Lock -> update, insert 순서 바꿔도 JPA 떄문에 X락 먼저 획득 안된다는 것을 보여줌
        // -> saveAllAndFlush를 호출해서 먼저 X락을 획득하고 S락을 획득하는 방식으로 바꿀 것!! ★★★
//        products.forEach(product -> {
//            productValues.stream()
//                    .filter(productDto -> productDto.getProductId().equals(product.getId()))
//                    .findFirst()
//                    .ifPresent(productDto -> {
//                        product.decrease(productDto.getQuantity());
//            });
//        });
//        productRepository.saveAllAndFlush(products);
        //========================================================================================================

        // products 리스트를 Map으로 변환하여 사용하면 편리합니다.
        Map<Long, Product> productMap = products.stream().collect(Collectors.toMap(Product::getId, product -> product));

        return productValues.stream().map(pv -> {
            Product product = productMap.get(pv.getProductId());
            OrderDetail orderDetail = OrderDetail.create(order, product, pv.getQuantity(), StatusCodeType.ORDER_INIT.getCode(), dateTimeHolder);
            order.addOrderDetail(orderDetail);
            return orderDetail;
        }).collect(Collectors.toList());
    }

    private Order createOrder(CreateOrderDto createOrderDto) {
        Member member = memberRepository.findById(createOrderDto.getMemberId()).orElseThrow(() -> new IllegalArgumentException("Member not found"));
        Address address = member.getAddress();
        String orderName = createOrderDto.getOrderName();
        String orderNo = createOrderDto.getOrderNo();
        Long totalOrderPrice = createOrderDto.getTotalOrderPrice();
        Long realOrderPrice = createOrderDto.getRealOrderPrice();
        Long totalDiscountPrice = createOrderDto.getTotalDiscountPrice();
        String tid = createOrderDto.getTid();

        return Order.create(member, address, orderName, orderNo, totalOrderPrice, realOrderPrice, totalDiscountPrice, tid);
    }
}
