package sm.order_project.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sm.order_project.api.domain.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

}
