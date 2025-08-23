package sm.order_project.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sm.order_project.api.domain.Member;

public interface MemberRepository extends JpaRepository<Member,Long> {
}
