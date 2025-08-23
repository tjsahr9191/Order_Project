package sm.order_project.api.kakao;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CanceledAmount {
    private Long total;
    private Long discount;

    @Builder
    public CanceledAmount(Long total, Long discount) {
        this.total = total;
        this.discount = discount;
    }
}
