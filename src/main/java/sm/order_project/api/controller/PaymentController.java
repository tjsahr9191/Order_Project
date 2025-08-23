package sm.order_project.api.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.HandlerMethod;
import sm.order_project.api.common.ApiResponse;
import sm.order_project.api.dto.CompleteOrderResponse;
import sm.order_project.api.kakao.KakaoPayApproveResponse;
import sm.order_project.api.service.PaymentService;

import java.time.LocalDateTime;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;

//    @ExceptionHandler(value = Exception.class)
//    @ResponseStatus(INTERNAL_SERVER_ERROR)
//    public ErrorResult.Response kakaoApiExHandler(Exception e, HttpServletRequest httpRequest, HandlerMethod handlerMethod) {
//
//        StringBuilder sb = new StringBuilder();
//
//        // 사용쿠폰, 재고, 주문, 상세주문 전부 롤백
//        quitPaymentService.cancel(getOrderNoFromUri(httpRequest.getRequestURI()));
//
//        ErrorResult errorResult = ErrorResult.builder()
//                .code(NOT_FOUND.value())
//                .status(NOT_FOUND)
//                .path(String.valueOf(sb.append(httpRequest.getMethod()).append(httpRequest.getRequestURI())))
//                .errorCode(NO_ERROR_CODE)
//                .message(e.getMessage())
//                .timestamp(LocalDateTime.now())
//                .className(handlerMethod.getBeanType().getName())
//                .methodName(handlerMethod.getMethod().getName())
//                .exceptionName(e.getClass().getName())
//                .build();
//
//        ErrorLoggerUtils.errorLog(errorResult);
//
//        return errorResult.toResponse();
//    }

    @GetMapping("/payment/kakao-pay/approval/{orderNo}") // 결제 승인 성공
    public ApiResponse<CompleteOrderResponse> createPayment(
            @RequestParam(name = "pg_token") String pgToken,
            @PathVariable(name = "orderNo") String orderNo) {

        // 1. kakaoPayService 로 결제 승인 요청 보내기
        KakaoPayApproveResponse kakaoResponse = paymentService.approve(orderNo, pgToken);

        // 2. Payment 생성 및 OrderDetails 업데이트
        CompleteOrderResponse response = paymentService.create(kakaoResponse);

        return ApiResponse.ok(response);
    }
}
