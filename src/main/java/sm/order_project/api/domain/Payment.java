package sm.order_project.api.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @Builder
    public Payment(String tid, PaymentMethod paymentMethod) {
        this.tid = tid;
        this.paymentMethod = paymentMethod;
    }

}
