package sm.order_project.api.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import sm.order_project.api.kakao.KakaoPayReadyResponse;

@Component
@Slf4j
public class MockOrderService {

    public KakaoPayReadyResponse ready() {
        try {
            Thread.sleep(100);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return KakaoPayReadyResponse.builder()
                .tid("T8abdf845f530b805c4d")
                .nextRedirectPcUrl("https://online-payment.kakaopay.com/mockup/bridge/pc/pg/one-time/payment/feb32bcac62c3e50b54b6459d242d2581debd6a9a66beddba2802774e28de129")
                .nextRedirectAppUrl("https://online-payment.kakaopay.com/mockup/bridge/mobile-app/pg/one-time/payment/feb32bcac62c3e50b54b6459d242d2581debd6a9a66beddba2802774e28de129")
                .nextRedirectMobileUrl("https://online-payment.kakaopay.com/mockup/bridge/mobile-web/pg/one-time/payment/feb32bcac62c3e50b54b6459d242d2581debd6a9a66beddba2802774e28de129")
                .androidAppScheme("kakaotalk://kakaopay/pg?url=https://online-pay.kakaopay.com/pay/mockup/feb32bcac62c3e50b54b6459d242d2581debd6a9a66beddba2802774e28de129")
                .iosAppScheme("kakaotalk://kakaopay/pg?url=https://online-pay.kakaopay.com/pay/mockup/feb32bcac62c3e50b54b6459d242d2581debd6a9a66beddba2802774e28de129")
                .createdAt("2025-08-25T12:59:00")
                .build();
    }

}