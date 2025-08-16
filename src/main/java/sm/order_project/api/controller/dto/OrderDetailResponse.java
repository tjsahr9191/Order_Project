package sm.order_project.api.controller.dto;

import lombok.Builder;
import lombok.Getter;
import sm.order_project.api.domain.DeliveryStatus;
import sm.order_project.api.domain.Order;
import sm.order_project.api.domain.OrderDetail;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class OrderDetailResponse {

    private Long orderId;
    private Long totalAmount;
    private LocalDateTime orderDate;
    private DeliveryStatus deliveryStatus;
    private List<ProductInfo> productInfos;

    public static OrderDetailResponse of(Order order) {
        return OrderDetailResponse.builder()
                .orderId(order.getId())
                .orderDate(order.getCreatedAt())
                .totalAmount(order.calculateTotalAmount())
                .deliveryStatus(order.getDeliveryStatus())
                .productInfos(createProductInfos(order))
                .build();
    }

    private static List<ProductInfo> createProductInfos(Order order) {
//        return null;
        System.out.println(order.getOrderDetails().getOrderDetailList().size());
        return order.getOrderDetails().getOrderDetailList().stream()
                .map(ProductInfo::of)
                .toList();
    }

    @Getter
    @Builder
    public static class ProductInfo {
        private Long id;
        private String name;
        private int quantity;
        private int price;

        public static ProductInfo of(OrderDetail orderDetail) {
            return ProductInfo.builder()
                    .id(orderDetail.getProduct().getId())
                    .name(orderDetail.getProduct().getName())
                    .quantity(orderDetail.getQuantity())
                    .price(orderDetail.getPrice())
                    .build();
        }
    }

}
