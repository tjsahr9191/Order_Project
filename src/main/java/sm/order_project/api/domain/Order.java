package sm.order_project.api.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@Table(name = "orders")
@NoArgsConstructor(access = PROTECTED)
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Embedded // OrderDetails 클래스를 이 엔티티에 포함시킴
    private OrderDetails orderDetails;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "payment_id")
    private Payment payment;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;

    @Embedded
    private Address address;

    private String name;

    private String no;

    private String tid;

    private Long totalAmount;

    @Builder
    public Order(OrderDetails orderDetails, Member member, Payment payment, Delivery delivery, Address address, String name, String no, String tid, Long totalAmount) {
        this.orderDetails = orderDetails;
        this.member = member;
        this.payment = payment;
        this.delivery = delivery;
        this.address = address;
        this.name = name;
        this.no = no;
        this.tid = tid;
        this.totalAmount = totalAmount;
    }

    public void addOrderDetail(OrderDetail orderDetail) {
        this.orderDetails.add(orderDetail);
        orderDetail.updateOrder(this);
    }

    public DeliveryStatus getDeliveryStatus() {
        return delivery.getDeliveryStatus();
    }

}
