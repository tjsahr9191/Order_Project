package sm.order_project.api.service.Facade;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import sm.order_project.api.dto.CreateOrderDto;
import sm.order_project.api.exception.CustomLogicException;
import sm.order_project.api.repository.OrderRepository;
import sm.order_project.api.service.OrderService;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class NamedLockOrderFacade {

    private final OrderService orderService;
    private final OrderRepository orderRepository;

    private static final long RETRY_DELAY_MS = 100; // 재시도 사이의 대기 시간 (밀리초)
    private static final int LOCK_TIMEOUT_SECONDS = 3; // 락 획득을 위한 대기 시간 (초)
    private static final int MAX_RETRIES = 30;

    public void createOrder(CreateOrderDto createOrderDto) throws InterruptedException {
        // Named Lock은 여러 상품을 동시에 잠그기 복잡하므로,
        // 이 예제에서는 첫 번째 상품 ID를 기준으로 잠금을 겁니다.
        // 실제 운영 환경에서는 모든 상품 ID에 대해 잠금을 획득하거나 다른 전략이 필요할 수 있습니다.
        if (createOrderDto.getProductValues() == null || createOrderDto.getProductValues().isEmpty()) {
            throw new IllegalArgumentException("주문할 상품이 없습니다.");
        }

        int retryCount = 0;

        while (true) {
            String lockName = null;
            try {
                // 주문에 포함된 모든 상품 ID를 정렬하여 순서에 따른 데드락 방지
                List<Long> productIds = createOrderDto.getProductValues().stream()
                        .map(CreateOrderDto.ProductDto::getProductId)
                        .sorted()
                        .toList();

                // 모든 상품에 대해 순차적으로 락 획득
                for (Long productId : productIds) {
                    lockName = "product_" + productId;
                    int lockResult = orderRepository.getLock(lockName, LOCK_TIMEOUT_SECONDS);
                    if (lockResult <= 0) {
                        log.warn("락 획득 실패 - product_id: {}, orderNo: {}", productId, createOrderDto.getOrderNo());
                        // 획득 실패 시, 이미 획득한 락이 있다면 해제해야 함
                        Thread.sleep(RETRY_DELAY_MS);
//                        throw new RuntimeException("락 획득 실패");
                    }
                }

                // 모든 락을 획득했으면 비즈니스 로직 호출
                orderService.create(createOrderDto);

                log.info("주문 생성 성공 - orderNo: {}, 총 시도 횟수: {}",
                        createOrderDto.getOrderNo(), retryCount + 1);
                return; // 성공 시 루프 종료

            } catch (CustomLogicException e) {
                throw e;
            } catch (Exception e) {
                retryCount++;
                log.warn("주문 생성 재시도 - orderNo: {}, 현재 시도 횟수: {}, error: {}",
                        createOrderDto.getOrderNo(), retryCount, e.getMessage());
                if (retryCount > MAX_RETRIES) {
                    log.error("최대 재시도 횟수({}) 초과. 주문 생성 실패 - orderNo: {}",
                            MAX_RETRIES, createOrderDto.getOrderNo());
                    throw new RuntimeException("주문 생성에 실패했습니다 (최대 재시도 초과).", e);
                }
                // 잠시 대기 후 재시도
                Thread.sleep(RETRY_DELAY_MS);

            } finally {
                // 획득했던 모든 락을 해제 (순서는 상관 없음)
                if (createOrderDto.getProductValues() != null) {
                    createOrderDto.getProductValues().forEach(p -> {
                        String finalLockName = "product_" + p.getProductId();
                        int releaseResult = orderRepository.releaseLock(finalLockName);
                        if (releaseResult <= 0) {
                            log.error("락 해제 중 문제 발생 - lockName: {}", finalLockName);
                        }
                    });
                }
            }
        }
    }
}