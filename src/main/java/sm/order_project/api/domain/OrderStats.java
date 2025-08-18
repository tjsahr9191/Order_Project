package sm.order_project.api.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "order_stats")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderStats {

    @Id @Column(name = "order_stats_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id") // DB 컬럼명을 명시적으로 지정
    private Long memberId;

    private String email;
    private int orderCount;
    private long totalAmount;
    private double avgAmount;
    private LocalDateTime lastOrderDate;
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    public void updateTimestamp() {
        this.updatedAt = LocalDateTime.now();
    }

}
