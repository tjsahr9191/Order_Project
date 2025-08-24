package sm.order_project.api.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sm.order_project.api.domain.Product;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product,Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Product p where p.id in :productIds")
    List<Product> findAllByIdInWithPessimisticLock(List<Long> productIds);

    @Lock(LockModeType.OPTIMISTIC)
    @Query("select p from Product p where p.id in :productIds")
    List<Product> findAllByIdInWithOptimisticLock(List<Long> productIds);

    @Query(value = "SELECT GET_LOCK(:lockName, :timeoutSeconds)", nativeQuery = true)
    Integer getLock(@Param("lockName") String lockName, @Param("timeoutSeconds") int timeoutSeconds);

    @Query(value = "SELECT RELEASE_LOCK(:lockName)", nativeQuery = true)
    Integer releaseLock(@Param("lockName") String lockName);

}
