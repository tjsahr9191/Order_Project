package sm.order_project.api.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sm.order_project.api.common.DateTimeHolder;
import sm.order_project.api.exception.CustomLogicException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.*;
import static lombok.AccessLevel.PROTECTED;
import static sm.order_project.api.exception.ErrorCode.QUANTITY_EXCEEDED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class OrderDetail extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "order_detail_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    private int quantity;

    private int price;

    @Builder
    private OrderDetail(Order order, Product product, int quantity, int price) {
        this.order = order;
        this.product = product;
        this.quantity = quantity;
        this.price = price;
    }

    public void updateOrder(Order order) {
        this.order = order;
    }

    public static OrderDetail create(Order order, Product product, Long quantity, String statusCode, DateTimeHolder dateTimeHolder) {
        return OrderDetail.builder()
                .order(order)
                .product(product)
                .price(Math.toIntExact(product.getPrice()))
                .quantity(Math.toIntExact(quantity))
                .build();
    }

}
