package sm.order_project.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sm.order_project.api.controller.dto.OrderDetailResponse;
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

}
