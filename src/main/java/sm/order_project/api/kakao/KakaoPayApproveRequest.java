package sm.order_project.api.kakao;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import sm.order_project.api.domain.Order;

import static sm.order_project.api.kakao.KakaoPayConfig.ONE_TIME_CID;

@Getter
public class KakaoPayApproveRequest {

    private String cid;
    private String tid;
    @JsonProperty("partner_order_id")
    private String partnerOrderId;
    @JsonProperty("partner_user_id")
    private String partnerUserId;

    @JsonProperty("pg_token")
    private String pgToken;

    @JsonProperty("total_amount")
    private Long totalAmount;

    @Builder
    public KakaoPayApproveRequest(String cid, String tid, String partnerOrderId, String partnerUserId, String pgToken, Long totalAmount) {
        this.cid = cid;
        this.tid = tid;
        this.partnerOrderId = partnerOrderId;
        this.partnerUserId = partnerUserId;
        this.pgToken = pgToken;
        this.totalAmount = totalAmount;
    }

    public static KakaoPayApproveRequest create(Order order, String pgToken) {
        return KakaoPayApproveRequest.builder()
                .pgToken(pgToken)
                .partnerUserId(order.getMember().getId().toString())
                .partnerOrderId(order.getNo())
                .tid(order.getTid())
                .totalAmount(order.getTotalAmount())
                .cid(ONE_TIME_CID)
                .build();
    }

}
