package sm.order_project.api.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderDetail> orderDetails;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "payment_id")
    private Payment payment;

    @Embedded
    private Address address;

    private String name;

    private String no;

    private int totalPrice;

    private String tid;

    @Builder
    public Order(Member member, List<OrderDetail> orderDetails, Payment payment, Address address, String name, String no, int totalPrice, String tid) {
        this.member = member;
        this.orderDetails = orderDetails;
        this.payment = payment;
        this.address = address;
        this.name = name;
        this.no = no;
        this.totalPrice = totalPrice;
        this.tid = tid;
    }

    public void addOrderDetail(OrderDetail orderDetail) {
        this.orderDetails.add(orderDetail);
        orderDetail.updateOrder(this);
    }

}
