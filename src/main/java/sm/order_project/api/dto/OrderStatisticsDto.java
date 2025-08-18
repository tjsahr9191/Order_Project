package sm.order_project.api.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Data;
import sm.order_project.api.domain.OrderStats;

@Data
public class OrderStatisticsDto {

    private long memberId;
    private long totalOrders;
    private long totalAmount;
    private double averageAmount;

    @QueryProjection
    @Builder
    public OrderStatisticsDto(long memberId, long totalOrders, long totalAmount, double averageAmount) {
        this.memberId = memberId;
        this.totalOrders = totalOrders;
        this.totalAmount = totalAmount;
        this.averageAmount = averageAmount;
    }

    public static OrderStatisticsDto of(OrderStats orderStats) {
        return OrderStatisticsDto.builder()
                .memberId(orderStats.getId())
                .totalOrders(orderStats.getOrderCount())
                .totalAmount(orderStats.getTotalAmount())
                .averageAmount(orderStats.getAvgAmount())
                .build();
    }
}