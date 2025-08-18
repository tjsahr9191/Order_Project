package sm.order_project.api.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderStatisticsResponse {
    private String memberEmail;
    private Long totalOrders;
    private Long totalAmount;
    private Double averageAmount;
}