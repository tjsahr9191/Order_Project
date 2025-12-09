package sm.order_project.api.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QOrderDetails is a Querydsl query type for OrderDetails
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QOrderDetails extends BeanPath<OrderDetails> {

    private static final long serialVersionUID = 1962468592L;

    public static final QOrderDetails orderDetails1 = new QOrderDetails("orderDetails1");

    public final ListPath<OrderDetail, QOrderDetail> orderDetails = this.<OrderDetail, QOrderDetail>createList("orderDetails", OrderDetail.class, QOrderDetail.class, PathInits.DIRECT2);

    public QOrderDetails(String variable) {
        super(OrderDetails.class, forVariable(variable));
    }

    public QOrderDetails(Path<? extends OrderDetails> path) {
        super(path.getType(), path.getMetadata());
    }

    public QOrderDetails(PathMetadata metadata) {
        super(OrderDetails.class, metadata);
    }

}

