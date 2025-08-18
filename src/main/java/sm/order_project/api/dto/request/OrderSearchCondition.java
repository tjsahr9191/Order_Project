package sm.order_project.api.dto.request;

import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import sm.order_project.api.domain.DeliveryStatus;
import sm.order_project.api.domain.PaymentMethod;

import java.time.LocalDate;

@Data
@Builder
public class OrderSearchCondition {

    private Long memberId; // 회원 이름

    private DeliveryStatus deliveryStatus; // 주문 상태 (READY, COMP)

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate orderDateFrom; // 주문일자 (시작)

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate orderDateTo; // 주문일자 (종료)

    private PaymentMethod paymentMethod; // 결제 수단 (CARD, BANK_TRANSFER)

    private Integer minPrice; // 최소 주문 금액

    private String sortBy; // 정렬 기준 (예: "latest", "priceDesc", "priceAsc")

}