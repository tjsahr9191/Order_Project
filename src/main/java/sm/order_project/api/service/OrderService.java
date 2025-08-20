package sm.order_project.api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sm.order_project.api.controller.OrderController;
import sm.order_project.api.domain.OrderStats;
import sm.order_project.api.dto.OrderStatisticsDto;
import sm.order_project.api.dto.response.OrderDetailResponse;
import sm.order_project.api.dto.request.OrderSearchCondition;
import sm.order_project.api.dto.response.SimpleOrderResponse;
import sm.order_project.api.domain.Order;
import sm.order_project.api.repository.OrderQueryRepository;
import sm.order_project.api.repository.OrderRepository;
import sm.order_project.api.repository.OrderStatsRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderQueryRepository orderQueryRepository;
    private final OrderStatsRepository orderStatsRepository;

    public OrderDetailResponse findOrderDetails(Long id) {

        Order foundOrder = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        return OrderDetailResponse.of(foundOrder);
    }

    public Page<SimpleOrderResponse> findOrdersByCondition(OrderSearchCondition condition, Pageable pageable) {

        Page<Order> orderPage = orderRepository.searchOrders(condition, pageable);

        return orderPage.map(SimpleOrderResponse::of);
    }

    public Page<OrderStatisticsDto> getOrderStatistics(Long minAmount, Pageable pageable) {
        Page<OrderStats> orderStats = orderStatsRepository.findByTotalAmountGreaterThanEqual(
                minAmount != null ? minAmount : 0L,
                pageable
        );

        return orderStats.map(OrderStatisticsDto::of);
    }

//    public Page<OrderStatisticsDto> getOrderStatistics(Long minAmount, Pageable pageable) {
//        if (minAmount == null) {
//            minAmount = 0L;
//        }
//
//        return orderQueryRepository.getOrderStatisticsQuerydsl(minAmount, pageable);
//    }

//    @Scheduled(cron = "0 */3 * * * *")
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

        List<SimpleOrderResponse> response = pagedOrders.getContent().stream()
                .map(SimpleOrderResponse::of) // GetOrderDto -> GetOrderHttp.Response
                .toList();

        return new PageImpl<>(response, pageable, pagedOrders.getTotalElements());
    }
}
