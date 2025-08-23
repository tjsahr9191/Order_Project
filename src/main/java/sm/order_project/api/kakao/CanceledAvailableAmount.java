package sm.order_project.api.kakao;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CanceledAvailableAmount {

    private Long total;
    private Long discount;

    @Builder
    public CanceledAvailableAmount(Long total, Long discount) {
        this.total = total;
        this.discount = discount;
    }

}
