package sm.order_project.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sm.order_project.api.domain.OrderDetail;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
}
