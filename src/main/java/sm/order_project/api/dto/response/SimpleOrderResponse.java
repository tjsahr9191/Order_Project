package sm.order_project.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import sm.order_project.api.domain.Address;
import sm.order_project.api.domain.DeliveryStatus;
import sm.order_project.api.domain.Order;

import java.time.LocalDateTime;

@Data
public class SimpleOrderResponse {

    private Long orderId;
    private String orderNo;
    private Long memberId;
    private long totalPrice;
    private DeliveryStatus deliveryStatus;
    private Address address;
    private LocalDateTime orderDate;

    @Builder
    private SimpleOrderResponse(Long orderId, String orderNo, Long memberId, long totalPrice, DeliveryStatus deliveryStatus, Address address, LocalDateTime orderDate) {
        this.orderId = orderId;
        this.orderNo = orderNo;
        this.memberId = memberId;
        this.totalPrice = totalPrice;
        this.deliveryStatus = deliveryStatus;
        this.address = address;
        this.orderDate = orderDate;
    }

    public static SimpleOrderResponse of(Order order) {
        return SimpleOrderResponse.builder()
                .orderId(order.getId())
                .orderNo(order.getNo())
                .memberId(order.getMember().getId())
                .totalPrice(order.getTotalAmount())
                .deliveryStatus(order.getDeliveryStatus())
                .address(order.getAddress())
                .orderDate(order.getCreatedAt())
                .build();
    }

}