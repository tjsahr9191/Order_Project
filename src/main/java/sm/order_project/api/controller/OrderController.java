package sm.order_project.api.controller;

import jakarta.validation.Valid;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sm.order_project.api.common.ApiResponse;
import sm.order_project.api.dto.CreateOrderRequest;
import sm.order_project.api.dto.CreateOrderResponse;
import sm.order_project.api.dto.OrderStatisticsDto;
import sm.order_project.api.dto.response.OrderDetailResponse;
import sm.order_project.api.dto.request.OrderSearchCondition;
import sm.order_project.api.dto.response.OrderStatisticsResponse;
import sm.order_project.api.dto.response.SimpleOrderResponse;
import sm.order_project.api.kakao.KakaoPayReadyResponse;
import sm.order_project.api.service.Facade.TestOrderFacade;
import sm.order_project.api.service.OrderService;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final TestOrderFacade testOrderFacade;

    @GetMapping("/index")
    public String index() {
        return "index";
    }

    @GetMapping("/{id}")
    public ApiResponse<OrderDetailResponse> getOrderDetails(@PathVariable Long id) {
        return ApiResponse.ok(orderService.findOrderDetails(id));
    }

    @GetMapping
    public ApiResponse<Page<SimpleOrderResponse>> getOrders(
            @ModelAttribute OrderSearchCondition condition, Pageable pageable) {
        return ApiResponse.ok(orderService.findOrdersByCondition(condition, pageable));
    }

    @GetMapping("/v2")
    public ApiResponse<Page<SimpleOrderResponse>> getOrders2(
            @RequestParam Long memberId,
            @RequestParam Long minAmount,
            @RequestParam(name = "year", required = false) String year,
            @RequestParam(name = "keyword", required = false) String keyword,
            @PageableDefault(size = 5, sort = {"createdAt"}, direction = Sort.Direction.DESC) Pageable pageable) {

        Condition condition = Condition.builder()
                .memberId(memberId)
                .minAmount(minAmount)
                .year(year)
                .keyword(keyword)
                .build();

        return ApiResponse.ok(orderService.search(pageable, condition));
    }

    @Getter
    public static class Condition {
        private String year;
        private String keyword;
        private Long memberId;
        private Long minAmount;

        @Builder
        public Condition(String year, String keyword, Long memberId, Long minAmount) {
            this.year = year;
            this.keyword = keyword;
            this.memberId = memberId;
            this.minAmount = minAmount;
        }
    }

    @GetMapping("/statistics/stats/v1")
    public ApiResponse<Page<OrderStatisticsDto>> getMemberStatistics(
            @RequestParam(required = false) Long minAmount,
            Pageable pageable) {

        return ApiResponse.ok(orderService.getOrderStatisticsv1(minAmount, pageable));
    }

    @GetMapping("/stats")
    public ApiResponse<Page<OrderStatisticsDto>> getOrderStatistics(
            @RequestParam(required = false) Long minAmount,
            Pageable pageable) {
        return ApiResponse.ok(orderService.getOrderStatistics(minAmount, pageable));
    }

    @PostMapping
    public ApiResponse<CreateOrderResponse> createOrder (
            @RequestParam Long memberId,
            @Valid @RequestBody CreateOrderRequest request) {

//        CreateOrderResponse createOrderResponse = testOrderFacade.ready(memberId, request);
        // 1. orderNo 생성
        String orderNo = UUID.randomUUID().toString();

        // 2. 카카오페이 결제 준비 요청
//        long startTime = System.currentTimeMillis();
        KakaoPayReadyResponse kakaoReadyResponse = orderService.ready(request, orderNo, memberId);
//        long executionTime = System.currentTimeMillis() - startTime;
//        log.info("걸린 시간 = {}",  executionTime);
        String tid = kakaoReadyResponse.getTid();

        // 3. 주문 생성
        orderService.create(request.toDto(orderNo, tid, memberId));

        return ApiResponse.ok(CreateOrderResponse.of(kakaoReadyResponse));
    }

}
