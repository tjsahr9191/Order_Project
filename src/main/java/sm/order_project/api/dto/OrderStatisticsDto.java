package sm.order_project.api.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

@Data
public class OrderStatisticsDto {

    private long memberId;
    private long totalOrders;
    private long totalAmount;
    private double averageAmount;

    @QueryProjection
    public OrderStatisticsDto(long memberId, long totalOrders, long totalAmount, double averageAmount) {
        this.memberId = memberId;
        this.totalOrders = totalOrders;
        this.totalAmount = totalAmount;
        this.averageAmount = averageAmount;
    }

}