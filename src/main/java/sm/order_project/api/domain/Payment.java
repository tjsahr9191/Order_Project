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

    @OneToOne(mappedBy = "payment", cascade = CascadeType.ALL, orphanRemoval = true)
    private Order order;

    private String tid;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Builder
    public Payment(Order order, String tid, PaymentMethod paymentMethod) {
        this.order = order;
        this.tid = tid;
        this.paymentMethod = paymentMethod;
    }

}
