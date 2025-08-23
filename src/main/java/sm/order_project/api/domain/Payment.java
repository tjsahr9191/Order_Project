package sm.order_project.api.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "payment_id")
    private Long id;

    private String tid;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    private String orderNo;

    private LocalDateTime approvedAt;

    // 카드 결제
    @Embedded
    private CardInfo cardInfo;

    // 결제 금액 정보
    @Embedded
    private Amount amount;

    @Builder
    public Payment(String tid, PaymentMethod paymentMethod, String orderNo, LocalDateTime approvedAt, CardInfo cardInfo, Amount amount) {
        this.tid = tid;
        this.paymentMethod = paymentMethod;
        this.orderNo = orderNo;
        this.approvedAt = approvedAt;
        this.cardInfo = cardInfo;
        this.amount = amount;
    }

    public static Payment create(String orderNo, String paymentMethod, String tid, CardInfo cardInfo, Amount amount, LocalDateTime approvedAt) {
        return Payment.builder()
                .orderNo(orderNo)
                .paymentMethod(PaymentMethod.valueOf(paymentMethod))
                .tid(tid)
                .cardInfo(cardInfo)
                .amount(amount)
                .approvedAt(approvedAt)
                .build();
    }


    public void changeStatusCode(String code) {

    }
}


