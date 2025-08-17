package sm.order_project.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sm.order_project.api.dto.response.OrderDetailResponse;
import sm.order_project.api.dto.request.OrderSearchCondition;
import sm.order_project.api.dto.response.SimpleOrderResponse;
import sm.order_project.api.domain.Order;
import sm.order_project.api.repository.OrderRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderDetailResponse findOrderDetails(Long id) {

        Order foundOrder = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        return OrderDetailResponse.of(foundOrder);
    }

    public Page<SimpleOrderResponse> findOrdersByCondition(OrderSearchCondition condition, Pageable pageable) {

        Page<Order> orderPage = orderRepository.searchOrders(condition, pageable);

        return orderPage.map(SimpleOrderResponse::of);
    }

}
