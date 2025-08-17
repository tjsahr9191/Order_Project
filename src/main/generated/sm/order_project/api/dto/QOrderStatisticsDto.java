package sm.order_project.api.dto;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.ConstructorExpression;
import javax.annotation.processing.Generated;

/**
 * sm.order_project.api.dto.QOrderStatisticsDto is a Querydsl Projection type for OrderStatisticsDto
 */
@Generated("com.querydsl.codegen.DefaultProjectionSerializer")
public class QOrderStatisticsDto extends ConstructorExpression<OrderStatisticsDto> {

    private static final long serialVersionUID = 1005775755L;

    public QOrderStatisticsDto(com.querydsl.core.types.Expression<Long> memberId, com.querydsl.core.types.Expression<Long> totalOrders, com.querydsl.core.types.Expression<Long> totalAmount, com.querydsl.core.types.Expression<Double> averageAmount) {
        super(OrderStatisticsDto.class, new Class<?>[]{long.class, long.class, long.class, double.class}, memberId, totalOrders, totalAmount, averageAmount);
    }

}

