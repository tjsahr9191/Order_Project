package sm.order_project.api.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QOrderStats is a Querydsl query type for OrderStats
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QOrderStats extends EntityPathBase<OrderStats> {

    private static final long serialVersionUID = 163809997L;

    public static final QOrderStats orderStats = new QOrderStats("orderStats");

    public final NumberPath<Double> avgAmount = createNumber("avgAmount", Double.class);

    public final StringPath email = createString("email");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final DateTimePath<java.time.LocalDateTime> lastOrderDate = createDateTime("lastOrderDate", java.time.LocalDateTime.class);

    public final NumberPath<Long> memberId = createNumber("memberId", Long.class);

    public final NumberPath<Integer> orderCount = createNumber("orderCount", Integer.class);

    public final NumberPath<Long> totalAmount = createNumber("totalAmount", Long.class);

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public QOrderStats(String variable) {
        super(OrderStats.class, forVariable(variable));
    }

    public QOrderStats(Path<? extends OrderStats> path) {
        super(path.getType(), path.getMetadata());
    }

    public QOrderStats(PathMetadata metadata) {
        super(OrderStats.class, metadata);
    }

}

