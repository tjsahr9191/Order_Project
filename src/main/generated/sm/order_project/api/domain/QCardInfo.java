package sm.order_project.api.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QCardInfo is a Querydsl query type for CardInfo
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QCardInfo extends BeanPath<CardInfo> {

    private static final long serialVersionUID = 1398648762L;

    public static final QCardInfo cardInfo = new QCardInfo("cardInfo");

    public final StringPath cardInstallMonth = createString("cardInstallMonth");

    public final StringPath cardIssuerName = createString("cardIssuerName");

    public final StringPath cardPurchaseName = createString("cardPurchaseName");

    public QCardInfo(String variable) {
        super(CardInfo.class, forVariable(variable));
    }

    public QCardInfo(Path<? extends CardInfo> path) {
        super(path.getType(), path.getMetadata());
    }

    public QCardInfo(PathMetadata metadata) {
        super(CardInfo.class, metadata);
    }

}

