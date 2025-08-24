package sm.order_project.api.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sm.order_project.api.domain.Order;
import sm.order_project.api.dto.request.OrderSearchCondition;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long>, OrderRepositoryCustom {

    Page<Order> searchOrders(OrderSearchCondition condition, Pageable pageable);

    @EntityGraph(attributePaths = {"member"})
    Optional<Order> findByNo(String no);

    @Query("SELECT o FROM Order o " +
            "JOIN o.orderDetails.orderDetails od " +
            "JOIN od.product p " +
            "WHERE o.no = :orderNo")
    Optional<Order> findByNoWithDetailsAndProduct(@Param("orderNo") String orderNo);

    @Query(value = "SELECT GET_LOCK(:lockName, :timeoutSeconds)", nativeQuery = true)
    int getLock(@Param("lockName") String lockName, @Param("timeoutSeconds") int timeoutSeconds);

    @Query(value = "SELECT RELEASE_LOCK(:lockName)", nativeQuery = true)
    int releaseLock(@Param("lockName") String lockName);

}
