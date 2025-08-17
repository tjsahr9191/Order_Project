package sm.order_project.api.repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import sm.order_project.api.domain.DeliveryStatus;
import sm.order_project.api.domain.Order;
import sm.order_project.api.domain.PaymentMethod;
import sm.order_project.api.dto.request.OrderSearchCondition;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static sm.order_project.api.domain.QDelivery.delivery;
import static sm.order_project.api.domain.QMember.member;
import static sm.order_project.api.domain.QOrder.order;
import static sm.order_project.api.domain.QPayment.payment;

@Repository
public class OrderRepositoryImpl implements OrderRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public OrderRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<Order> searchOrders(OrderSearchCondition condition, Pageable pageable) {

        List<Order> content = getPageOrdersBy(condition, pageable);

        return new PageImpl<>(content, pageable, getTotalFromCountQueryBy(condition));
    }

    private List<Order> getPageOrdersBy(OrderSearchCondition condition, Pageable pageable) {
        return queryFactory
                .selectFrom(order)
                .join(order.member, member)
                .join(order.delivery, delivery)
                .join(order.payment, payment)
                .where(
                        memberNameEq(condition.getMemberName()),
                        deliveryStatusEq(condition.getDeliveryStatus()),
                        orderDateBetween(
                                condition.getOrderDateFrom(),
                                condition.getOrderDateTo()
                        ),
                        paymentMethodEq(condition.getPaymentMethod()),
                        minPriceGoe(condition.getMinPrice())
                )
                .orderBy(getOrderSpecifier(condition.getSortBy()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    private Long getTotalFromCountQueryBy(OrderSearchCondition condition) {
        JPAQuery<Long> countQuery = createCountQueryBy(condition);

        return Optional.ofNullable(countQuery.fetchOne()).orElse(0L);
    }

    private JPAQuery<Long> createCountQueryBy(OrderSearchCondition condition) {
        return queryFactory
                .select(order.count())
                .from(order)
                .join(order.member, member)
                .join(order.delivery, delivery)
                .join(order.payment, payment)
                .where(
                        memberNameEq(condition.getMemberName()),
                        deliveryStatusEq(condition.getDeliveryStatus()),
                        orderDateBetween(
                                condition.getOrderDateFrom(),
                                condition.getOrderDateTo()
                        ),
                        paymentMethodEq(condition.getPaymentMethod()),
                        minPriceGoe(condition.getMinPrice())
                );
    }

    private OrderSpecifier<?> getOrderSpecifier(String sortBy) {
        if (!StringUtils.hasText(sortBy))
            return order.id.desc();

        return switch (sortBy) {
            case "priceDesc" -> order.totalPrice.desc();
            case "priceAsc" -> order.totalPrice.asc();
            default -> order.id.desc();
        };
    }

    private BooleanExpression memberNameEq(String memberName) {
        return StringUtils.hasText(memberName) ? member.name.contains(memberName) : null;
    }

    private BooleanExpression deliveryStatusEq(DeliveryStatus deliveryStatus) {
        return deliveryStatus != null ? delivery.deliveryStatus.eq(deliveryStatus) : null;
    }

    private BooleanExpression orderDateBetween(LocalDate dateFrom, LocalDate dateTo) {
        if (dateFrom == null || dateTo == null)
            return null;

        return order.createdAt.between(dateFrom.atStartOfDay(), dateTo.atTime(LocalTime.MAX));
    }

    private BooleanExpression paymentMethodEq(PaymentMethod paymentMethod) {
        return paymentMethod != null ? payment.paymentMethod.eq(paymentMethod) : null;
    }

    private BooleanExpression minPriceGoe(Integer minPrice) {
        return minPrice != null ? order.totalPrice.goe(minPrice) : null;
    }

}
