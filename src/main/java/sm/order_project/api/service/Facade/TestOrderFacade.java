package sm.order_project.api.service.Facade;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import sm.order_project.api.dto.CreateOrderRequest;
import sm.order_project.api.dto.CreateOrderResponse;
import sm.order_project.api.kakao.KakaoPayReadyResponse;
import sm.order_project.api.service.MockOrderService;
import sm.order_project.api.service.OrderService;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class TestOrderFacade {

    private final OrderService orderService;
    private final MockOrderService mockOrderService;

//    @Transactional
    public CreateOrderResponse ready(Long memberId, @Valid CreateOrderRequest request) {
        // 1. orderNo 생성
        String orderNo = UUID.randomUUID().toString();

        // 2. 카카오페이 결제 준비 요청
//        long startTime = System.currentTimeMillis();
//        KakaoPayReadyResponse kakaoReadyResponse = orderService.ready(request, orderNo, memberId);
//        long executionTime = System.currentTimeMillis() - startTime;
//        log.info("KakaoPayReadyResponse took {} ms", executionTime);
        KakaoPayReadyResponse kakaoReadyResponse = mockOrderService.ready();
        String tid = kakaoReadyResponse.getTid();

        // 3. 주문 생성
        orderService.create(request.toDto(orderNo, tid, memberId));

        return CreateOrderResponse.of(kakaoReadyResponse);
    }

}
