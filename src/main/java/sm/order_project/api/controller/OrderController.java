package sm.order_project.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import sm.order_project.api.common.ApiResponse;
import sm.order_project.api.dto.OrderStatisticsDto;
import sm.order_project.api.dto.response.OrderDetailResponse;
import sm.order_project.api.dto.request.OrderSearchCondition;
import sm.order_project.api.dto.response.SimpleOrderResponse;
import sm.order_project.api.service.OrderService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/{id}")
    public ApiResponse<OrderDetailResponse> getOrderDetails(@PathVariable Long id) {
        return ApiResponse.ok(orderService.findOrderDetails(id));
    }

    @GetMapping
    public ApiResponse<Page<SimpleOrderResponse>> getOrders(
            @ModelAttribute OrderSearchCondition condition, Pageable pageable) {
        return ApiResponse.ok(orderService.findOrdersByCondition(condition, pageable));
    }

    @GetMapping("/statistics/member-stats")
    public ApiResponse<Page<OrderStatisticsDto>> getMemberStatistics(
            @RequestParam(required = false) Long minAmount,
            Pageable pageable) {

        return ApiResponse.ok(orderService.getOrderStatistics(minAmount, pageable));
    }

}
