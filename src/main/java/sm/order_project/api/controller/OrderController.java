package sm.order_project.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sm.order_project.api.common.ApiResponse;
import sm.order_project.api.controller.dto.OrderDetailResponse;
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

}
