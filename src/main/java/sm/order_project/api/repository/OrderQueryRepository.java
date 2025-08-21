package sm.order_project.api.repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import sm.order_project.api.controller.OrderController;
import sm.order_project.api.domain.Order;
import sm.order_project.api.dto.OrderStatisticsDto;
import sm.order_project.api.dto.QOrderStatisticsDto;

import java.util.ArrayList;
import java.util.List;

import static com.querydsl.jpa.JPAExpressions.selectOne;
import static org.springframework.util.StringUtils.hasText;
import static sm.order_project.api.domain.QAddress.address;
import static sm.order_project.api.domain.QDelivery.delivery;
import static sm.order_project.api.domain.QMember.member;
import static sm.order_project.api.domain.QOrder.order;
import static sm.order_project.api.domain.QOrderDetail.orderDetail;
import static sm.order_project.api.domain.QPayment.payment;
import static sm.order_project.api.domain.QProduct.product;

@Repository
public class OrderQueryRepository {

    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    public OrderQueryRepository(EntityManager em) {
        this.em = em;
        queryFactory = new  JPAQueryFactory(em);
    }

    public Page<OrderStatisticsDto> getOrderStatisticsQuerydsl(Long minAmount, Pageable pageable) {
        List<OrderStatisticsDto> content = queryFactory
                .select(new QOrderStatisticsDto(
                        member.id,
                        order.id.count(),
                        order.totalAmount.longValue().sum(),
                        order.totalAmount.avg()
                ))
                .from(order)
                .join(order.member, member)
                .groupBy(member.id)
                .having(order.totalAmount.sum().goe(minAmount))
                .orderBy(order.totalAmount.sum().desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(member.id.count())
                .from(order)
                .join(order.member, member)
                .groupBy(member.id)
                .having(order.totalAmount.sum().goe(minAmount));

        Long total = (long) countQuery.fetch().size();

        return new PageImpl<>(content, pageable, total);
    }

    public Page<Order> findAllPaged(OrderController.Condition condition, Pageable pageable) { // Condition DTO가 있다고 가정
        // 1. 메인 컨텐츠 조회 쿼리 (ToOne 관계는 fetch join으로 최적화)
        List<Order> orders = queryFactory
                .selectFrom(order)
                .join(order.member, member).fetchJoin()
                .join(order.delivery, delivery).fetchJoin()
                .join(order.payment, payment).fetchJoin()
                .where(
                        // keyword를 포함하는 product가 이 주문에 존재하는지 서브쿼리로 확인
                        selectOne()
                                .from(orderDetail)
                                .join(orderDetail.product, product)
                                .where(
                                        orderDetail.order.id.eq(order.id),
                                        containsKeyword(condition.getKeyword())
                                ).exists()
                        ,
                        eqMemberId(condition.getMemberId()),
                        minTotalAmountGoe(condition.getMinAmount()) // ✅ totalAmount 필드 사용
                )
                .orderBy(createOrderSpecifiers(pageable.getSort()))
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetch();

        // 2. 카운트 쿼리
        JPAQuery<Long> countQuery = createCountQuery(condition);

        return PageableExecutionUtils.getPage(orders, pageable, countQuery::fetchOne);
    }

    private JPAQuery<Long> createCountQuery(OrderController.Condition condition) {
        return queryFactory
                .select(order.count())
                .from(order)
                .where(
                        selectOne()
                                .from(orderDetail)
                                .join(orderDetail.product, product)
                                .where(
                                        orderDetail.order.id.eq(order.id),
                                        containsKeyword(condition.getKeyword())
                                ).exists()
                        ,
                        eqMemberId(condition.getMemberId()),
                        minTotalAmountGoe(condition.getMinAmount()) // ✅ totalAmount 필드 사용
                );
    }

    private OrderSpecifier<?>[] createOrderSpecifiers(Sort sort) {
        List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();
        if (sort.isEmpty()) {
            orderSpecifiers.add(order.createdAt.desc()); // 기본 정렬
            return orderSpecifiers.toArray(new OrderSpecifier[0]);
        }

        sort.forEach(o -> {
            switch (o.getProperty()) {
                case "totalAmount":
                    OrderSpecifier<?> amountSpecifier = o.isAscending() ? order.totalAmount.asc() : order.totalAmount.desc();
                    orderSpecifiers.add(amountSpecifier);
                    break;
                case "createdAt":
                    OrderSpecifier<?> dateSpecifier = o.isAscending() ? order.createdAt.asc() : order.createdAt.desc();
                    orderSpecifiers.add(dateSpecifier);
                    break;
                // 다른 정렬 조건 추가 가능
            }
        });
        return orderSpecifiers.toArray(new OrderSpecifier[0]);
    }

    private BooleanExpression containsKeyword(String keyword) {
        return hasText(keyword) ? product.name.contains(keyword) : null;
    }

    private BooleanExpression eqMemberId(Long memberId) {
        return memberId != null ? order.member.id.eq(memberId) : null;
    }

    // ✅ totalAmount 필드를 사용하여 매우 간단해진 최소 금액 조건
    private BooleanExpression minTotalAmountGoe(Long minAmount) {
        return minAmount != null ? order.totalAmount.goe(minAmount) : null;
    }
}
