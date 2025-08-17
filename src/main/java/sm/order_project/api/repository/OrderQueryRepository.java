package sm.order_project.api.repository;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import sm.order_project.api.dto.OrderStatisticsDto;
import sm.order_project.api.dto.QOrderStatisticsDto;

import java.util.List;

import static sm.order_project.api.domain.QMember.member;
import static sm.order_project.api.domain.QOrder.order;

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
                        order.totalPrice.longValue().sum(),
                        order.totalPrice.avg()
                ))
                .from(order)
                .join(order.member, member)
                .groupBy(member.id)
                .having(order.totalPrice.sum().goe(minAmount))
                .orderBy(order.totalPrice.sum().desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(member.id.count())
                .from(order)
                .join(order.member, member)
                .groupBy(member.id)
                .having(order.totalPrice.sum().goe(minAmount));

        Long total = (long) countQuery.fetch().size();

        return new PageImpl<>(content, pageable, total);
    }

}
