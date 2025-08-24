package sm.order_project.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;
import sm.order_project.api.domain.Address;
import sm.order_project.api.domain.Member;
import sm.order_project.api.domain.Product;
import sm.order_project.api.dto.CreateOrderDto;
import sm.order_project.api.repository.MemberRepository;
import sm.order_project.api.repository.OrderDetailRepository;
import sm.order_project.api.repository.OrderRepository;
import sm.order_project.api.repository.ProductRepository;
import sm.order_project.api.service.Facade.OptimisticLockOrderFacade;
import sm.order_project.api.service.OrderService;
import sm.order_project.config.IntegrationTest;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Slf4j
@IntegrationTest
public class OrderServiceTest {

    @Autowired
    private OptimisticLockOrderFacade optimisticLockOrderFacade;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private OrderRepository orderRepository;

    private Member testMember;
    private Product testProduct;
    private final int INITIAL_STOCK = 50;

    @BeforeEach
    @Commit
    void setUp() {
        // 테스트 실행 전 데이터 초기화
        orderDetailRepository.deleteAllInBatch();
        orderRepository.deleteAllInBatch(); // 그 다음에 부모 테이블인 orders를 삭제합니다.

        // 다른 테이블들은 순서에 큰 영향이 없습니다.
        productRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();

        // 테스트용 Member, Product 생성 (Builder 패턴 사용)
        Address address = new Address("TestCity", "TestStreet", "12345");
        testMember = memberRepository.save(
                Member.builder()
                        .name("testUser")
                        .email("test@email.com")
                        .address(address)
                        .build()
        );

        testProduct = productRepository.save(
                Product.builder()
                        .name("Test Product")
                        .price(10000L)
                        .stock((long) INITIAL_STOCK)
                        .productNo(UUID.randomUUID().toString())
                        .build()
        );
    }

    @Test
    @DisplayName("단일 스레드에서는 재고가 정확하게 차감된다")
    void stockDecreaseWorksCorrectlyInSingleThread() {
        // given
        int numberOfAttempts = 100; // 재고(50개)보다 많은 주문 시도
        int successCount = 0;

        // when
        for (int i = 0; i < numberOfAttempts; i++) {
            try {
                CreateOrderDto requestDto = createOrderDtoForTestProduct(1L);
                orderService.create(requestDto);
                successCount++;
            } catch (Exception e) {
                log.info("재고 부족으로 인한 예상된 주문 실패: {}", e.getMessage());
            }
        }

        // then
        Product updatedProduct = productRepository.findById(testProduct.getId()).orElseThrow();
        long orderCount = orderRepository.count();

        System.out.println("초기 재고: " + INITIAL_STOCK);
        System.out.println("성공한 주문 수: " + successCount);
        System.out.println("DB에 저장된 최종 재고: " + updatedProduct.getStock());
        System.out.println("DB에 생성된 주문 수: " + orderCount);

        assertThat(successCount).isEqualTo(INITIAL_STOCK);
        assertThat(updatedProduct.getStock()).isZero();
        assertThat(orderCount).isEqualTo(INITIAL_STOCK);
    }

    @Test
    @DisplayName("Pessimistic Lock을 통해 여러 스레드의 동시 주문 요청을 올바르게 처리한다")
    void handleConcurrentOrdersCorrectlyWithPessimisticLock() throws InterruptedException {
        // given
        int numberOfThreads = 100; // 재고(50개)보다 많은 동시 요청
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        AtomicInteger successCount = new AtomicInteger(0);

        // when
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < numberOfThreads; i++) {
            executorService.submit(() -> {
                try {
                    CreateOrderDto requestDto = createOrderDtoForTestProduct(1L);
                    orderService.create(requestDto);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    log.info("재고 부족으로 인한 예상된 주문 실패");
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        long executionTime = System.currentTimeMillis() - startTime;

        // then
        Product updatedProduct = productRepository.findById(testProduct.getId()).orElseThrow();
        long orderCount = orderRepository.count();

        System.out.println("초기 재고: " + INITIAL_STOCK);
        System.out.println("성공한 주문 요청 수 (Atomic): " + successCount.get());
        System.out.println("DB에 저장된 최종 재고: " + updatedProduct.getStock());
        System.out.println("DB에 생성된 주문 수: " + orderCount);
        log.info("실행 시간 : {}ms", executionTime);

        // 가장 중요한 검증: 동시성 제어가 성공하여 최종 재고가 정확히 0이 되어야 한다.
        assertThat(updatedProduct.getStock()).isZero();
        assertThat(successCount.get()).isEqualTo(INITIAL_STOCK);
        assertThat(orderCount).isEqualTo(INITIAL_STOCK);
    }

    @Test
    @DisplayName("Optimistic Lock과 Facade 재시도 로직을 통해 동시 주문 요청을 올바르게 처리한다")
//    @Transactional(timeout = 10)
    void handleConcurrentOrdersCorrectlyWithOptimisticLock() throws InterruptedException {
        // given
        int numberOfThreads = 100; // 재고(50개)보다 많은 동시 요청
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        AtomicInteger successCount = new AtomicInteger(0);

        // when
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < numberOfThreads; i++) {
            executorService.submit(() -> {
                try {
                    // 2. Facade의 메소드를 호출하여 재시도 로직을 함께 테스트
                    CreateOrderDto requestDto = createOrderDtoForTestProduct(1L);
                    optimisticLockOrderFacade.createOrder(requestDto);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    // Facade에서 최대 재시도 횟수를 초과하면 예외가 발생할 수 있음
                    log.error("주문 생성 최종 실패: {}", e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();
        long executionTime = System.currentTimeMillis() - startTime;

        // then
        Product updatedProduct = productRepository.findById(testProduct.getId()).orElseThrow();
        long orderCount = orderRepository.count();

        System.out.println("초기 재고: " + INITIAL_STOCK);
        System.out.println("성공한 주문 요청 수 (Atomic): " + successCount.get());
        System.out.println("DB에 저장된 최종 재고: " + updatedProduct.getStock());
        System.out.println("DB에 생성된 주문 수: " + orderCount);
        log.info("실행 시간 : {}ms", executionTime);

        // 3. 비관적 락 테스트와 검증 내용은 동일 (결과는 같아야 함)
        // 가장 중요한 검증: 동시성 제어가 성공하여 최종 재고가 정확히 0이 되어야 한다.
        assertThat(updatedProduct.getStock()).isZero();
        assertThat(successCount.get()).isEqualTo(INITIAL_STOCK);
        assertThat(orderCount).isEqualTo(INITIAL_STOCK);
    }

    /**
     * 테스트용 CreateOrderDto를 생성하는 헬퍼 메소드
     */
    private CreateOrderDto createOrderDtoForTestProduct(long quantity) {
        CreateOrderDto.ProductDto productDto = CreateOrderDto.ProductDto.builder()
                .productId(testProduct.getId())
                .quantity(quantity)
                .build();

        return CreateOrderDto.builder()
                .memberId(testMember.getId())
                .orderNo(UUID.randomUUID().toString())
                .productValues(List.of(productDto))
                .build();
    }

}
