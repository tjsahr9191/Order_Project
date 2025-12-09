package sm.order_project.api.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QAmount is a Querydsl query type for Amount
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QAmount extends BeanPath<Amount> {

    private static final long serialVersionUID = -925238476L;

    public static final QAmount amount = new QAmount("amount");

    public final NumberPath<Long> discountAmount = createNumber("discountAmount", Long.class);

    public final NumberPath<Long> taxFreeAmount = createNumber("taxFreeAmount", Long.class);

    public final NumberPath<Long> totalAmount = createNumber("totalAmount", Long.class);

    public QAmount(String variable) {
        super(Amount.class, forVariable(variable));
    }

    public QAmount(Path<? extends Amount> path) {
        super(path.getType(), path.getMetadata());
    }

    public QAmount(PathMetadata metadata) {
        super(Amount.class, metadata);
    }

}

