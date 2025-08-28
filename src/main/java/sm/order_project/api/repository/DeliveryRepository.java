package sm.order_project.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sm.order_project.api.domain.Delivery;

public interface DeliveryRepository extends JpaRepository<Delivery,Long> {

}
