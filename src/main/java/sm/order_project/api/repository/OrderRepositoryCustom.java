package sm.order_project.api.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sm.order_project.api.domain.Order;
import sm.order_project.api.dto.request.OrderSearchCondition;

public interface OrderRepositoryCustom {

    Page<Order> searchOrders(OrderSearchCondition condition, Pageable pageable);

}
