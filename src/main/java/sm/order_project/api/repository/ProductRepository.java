package sm.order_project.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sm.order_project.api.domain.Product;

public interface ProductRepository extends JpaRepository<Product,Long> {
}
