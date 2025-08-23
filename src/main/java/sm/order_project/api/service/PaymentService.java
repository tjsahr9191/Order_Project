package sm.order_project.api.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import sm.order_project.api.domain.Amount;
import sm.order_project.api.domain.CardInfo;
import sm.order_project.api.domain.Order;
import sm.order_project.api.domain.Payment;
import sm.order_project.api.dto.CompleteOrderResponse;
import sm.order_project.api.exception.CustomLogicException;
import sm.order_project.api.exception.ErrorCode;
import sm.order_project.api.kakao.*;
import sm.order_project.api.repository.OrderRepository;
import sm.order_project.api.repository.PaymentRepository;

import java.time.LocalDateTime;

import static sm.order_project.api.domain.StatusCodeType.PAYMENT_COMPLETED;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final KakaoPayService kakaoPayService;

    public KakaoPayApproveResponse approve(String orderNo, String pgToken) throws RestClientException {
        Order order = orderRepository.findByNo(orderNo)
                .orElseThrow(EntityNotFoundException::new);

        validAmount(KakaoPayOrderRequest.create(KakaoPayConfig.ONE_TIME_CID, order.getTid()), order.getRealPrice());

        return kakaoPayService.approve(KakaoPayApproveRequest.create(order, pgToken));
    }

    private void validAmount(KakaoPayOrderRequest request, Long savedAmount) {
        Long totalAmount = kakaoPayService.getOrders(request).getAmount().getTotalAmount();

        if (totalAmount.equals(savedAmount)) {
            throw CustomLogicException.createBadRequestError(ErrorCode.INVALID_PAYMENT_AMOUNT);
        }
    }

    @Transactional
    public CompleteOrderResponse create(KakaoPayApproveResponse response) {

        // 1. Payment 엔티티 생성
        Payment payment = createPayment(response);
        paymentRepository.save(payment);

        // 2. Order - Payment 연관관계 매핑
        Order order = orderRepository.findByNoWithDetailsAndProduct(response.getPartnerOrderId())
                .orElseThrow(() -> CustomLogicException.createBadRequestError(ErrorCode.PAYMENT_NOT_FOUND));
        order.linkPayment(payment);

        // 3. orderDetail 의 statusCode 업데이트
        // payment.changeStatusCode(PAYMENT_COMPLETED.getCode());

        return CompleteOrderResponse.of(payment, order);
    }

    private Payment createPayment(KakaoPayApproveResponse response) {

        String orderNo = response.getPartnerOrderId();
        String paymentMethod = response.getPaymentMethodType();
        String tid = response.getTid();
        CardInfo cardInfo = response.getCardInfo();
        Amount amount = response.getAmount();
        LocalDateTime approvedAt = response.getApprovedAt();

        return Payment.create(orderNo, paymentMethod, tid, cardInfo, amount, approvedAt);
    }

}
