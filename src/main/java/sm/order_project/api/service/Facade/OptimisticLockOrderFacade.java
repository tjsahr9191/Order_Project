package sm.order_project.api.service.Facade;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Component;
import sm.order_project.api.dto.CreateOrderDto;
import sm.order_project.api.service.OrderService;

@Component
@RequiredArgsConstructor
@Slf4j
public class OptimisticLockOrderFacade {

    private final OrderService orderService;

    // 재시도 사이의 대기 시간 (밀리초)
    private static final long RETRY_DELAY_MS = 50;
    // 최대 재시도 횟수 (무한 루프 방지)
    private static final int MAX_RETRIES = 1000;

    public void createOrder(CreateOrderDto createOrderDto) throws InterruptedException {
        int retryCount = 0;

        while (true) {
            try {
                // 핵심 비즈니스 로직 호출
                orderService.create(createOrderDto);

                log.info("주문 생성 성공 - orderNo: {}, 총 시도 횟수: {}",
                        createOrderDto.getOrderNo(), retryCount + 1);
                return; // 성공 시 루프 종료

            } catch (ObjectOptimisticLockingFailureException e) {
                retryCount++;
                if (retryCount > MAX_RETRIES) {
                    log.error("최대 재시도 횟수({}) 초과. 주문 생성 실패 - orderNo: {}",
                            MAX_RETRIES, createOrderDto.getOrderNo());
                    throw new RuntimeException("주문 생성에 실패했습니다 (최대 재시도 초과).", e);
                }

                log.warn("재고 충돌 감지. 주문 생성 재시도 - orderNo: {}, 현재 시도 횟수: {}, error: {}",
                        createOrderDto.getOrderNo(), retryCount, e.getMessage());

                // 잠시 대기 후 재시도
                Thread.sleep(RETRY_DELAY_MS);
            }
        }
    }
}