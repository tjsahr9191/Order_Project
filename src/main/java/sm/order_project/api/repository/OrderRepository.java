package sm.order_project.api.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import sm.order_project.api.domain.Order;
import sm.order_project.api.domain.OrderDetail;
import sm.order_project.api.dto.request.OrderSearchCondition;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long>, OrderRepositoryCustom {

    Page<Order> searchOrders(OrderSearchCondition condition, Pageable pageable);

}
