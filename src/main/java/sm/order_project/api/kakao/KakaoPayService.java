package sm.order_project.api.kakao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import sm.order_project.api.exception.CustomLogicException;

import java.time.LocalDateTime;

import static org.springframework.http.HttpHeaders.*;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON;
import static sm.order_project.api.exception.ErrorCode.EXTERNAL_API_FAILURE;
import static sm.order_project.api.kakao.KakaoPayConfig.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoPayService {

    private final KakaoPayConfig kakaoPayConfig;

    public KakaoPayReadyResponse ready(KakaoPayReadyRequest request) {

        RestClient restClient = setup();

        return restClient.post()
                .uri(READY_END_POINT)
                .body(request)
                .retrieve()
                .body(KakaoPayReadyResponse.class);
    }


    //정기구독 1회차 , 단건결제
    public KakaoPayApproveResponse approve(KakaoPayApproveRequest request) throws CustomLogicException {

        try {
            RestClient restClient = setup();

            return restClient.post()
                    .uri(APPROVE_END_POINT)
                    .body(request)
                    .retrieve()
                    .body(KakaoPayApproveResponse.class);

        } catch (Exception e) {
            throw CustomLogicException.builderWithCause()
                    .cause(e)
                    .message(e.getMessage())
                    .errorCode(EXTERNAL_API_FAILURE)
                    .timestamp(LocalDateTime.now())
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }

    // 주문조회
    public KakaoPayOrderResponse getOrders(KakaoPayOrderRequest request){
        try {
            RestClient restClient = setup();

            return restClient.post()
                    .uri(ORDER_END_POINT)
                    .body(request)
                    .retrieve()
                    .body(KakaoPayOrderResponse.class);

        } catch (Exception e) {
            throw CustomLogicException.builderWithCause()
                    .cause(e)
                    .message(e.getMessage())
                    .errorCode(EXTERNAL_API_FAILURE)
                    .timestamp(LocalDateTime.now())
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }

    private RestClient setup() {

        return RestClient.builder()
                .baseUrl(KAKAO_BASE_URL)
                .defaultHeaders((httpHeaders -> {
                    httpHeaders.add(AUTHORIZATION, AUTH_SCHEME+ kakaoPayConfig.getSecretKeyDev());
                    httpHeaders.add(ACCEPT, APPLICATION_JSON+";charset=UTF-8");
                    httpHeaders.add(CONTENT_TYPE, MediaType.APPLICATION_JSON.toString());
                }))
                .build();
    }
}
