package sm.order_project.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sm.order_project.api.domain.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {


}
