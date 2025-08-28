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
    private OrderDetails orderDetails = new OrderDetails();

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

    private Long realPrice; // 실 결제 금액

    private Long totalDiscount; // 총 할인 금액 (쿠폰,포인트,멤버쉽)

    @Builder
    private Order(Member member, Payment payment, Delivery delivery, Address address, String name, String no, String tid, Long totalAmount, Long realPrice, Long totalDiscount) {
        this.member = member;
        this.payment = payment;
        this.delivery = delivery;
        this.address = address;
        this.name = name;
        this.no = no;
        this.tid = tid;
        this.totalAmount = totalAmount;
        this.realPrice = realPrice;
        this.totalDiscount = totalDiscount;
    }

    public static Order create(Member member, Address address, String orderName, String orderNo, Long totalPrice, Long realPrice, Long totalDiscount, String tid, Delivery delivery) {
        return Order.builder()
                .member(member)
                .delivery(delivery)
                .address(address)
                .name(orderName)
                .no(orderNo)
                .totalAmount(totalPrice)
                .realPrice(realPrice)
                .totalDiscount(totalDiscount)
                .tid(tid)
                .build();
    }

    public void addOrderDetail(OrderDetail orderDetail) {
        this.orderDetails.add(orderDetail);
        orderDetail.updateOrder(this);
    }

    public DeliveryStatus getDeliveryStatus() {
        return delivery.getDeliveryStatus();
    }

    public void stockDecrease() {
        orderDetails.stockDecrease();
    }

    public void linkPayment(Payment payment) {
        this.payment = payment;
//        payment.linkOrder(this);
    }

}
