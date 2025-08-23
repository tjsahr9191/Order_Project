package sm.order_project.api.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sm.order_project.api.domain.*;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class CompleteOrderResponse {

    private String paymentMethodType;
    private String orderName;
    private String approvedAt;
    private Long totalAmount;
    private Long discountAmount;
    private Long taxFreeAmount;
    private String cardIssuerName;
    private Long cardInstallMonth;
    private Address addressValue;
    private List<ProductInfoDto> productInfos;

    @Builder
    private CompleteOrderResponse(String paymentMethodType, String orderName, String approvedAt, Long totalAmount, Long discountAmount, Long taxFreeAmount, String cardIssuerName, Long cardInstallMonth, Address addressValue, List<ProductInfoDto> productInfos) {
        this.paymentMethodType = paymentMethodType;
        this.orderName = orderName;
        this.approvedAt = approvedAt;
        this.totalAmount = totalAmount;
        this.discountAmount = discountAmount;
        this.taxFreeAmount = taxFreeAmount;
        this.cardIssuerName = cardIssuerName;
        this.cardInstallMonth = cardInstallMonth;
        this.addressValue = addressValue;
        this.productInfos = productInfos;
    }

    public static CompleteOrderResponse of(Payment payment, Order order) {
        CardInfo cardInfo = payment.getCardInfo();
        return CompleteOrderResponse.builder()
                .paymentMethodType(String.valueOf(payment.getPaymentMethod()))
                .orderName(order.getName())
                .approvedAt(payment.getApprovedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .cardIssuerName(cardInfo != null ? cardInfo.getCardIssuerName() : null)
                .cardInstallMonth(cardInfo != null ? Long.parseLong(payment.getCardInfo().getCardInstallMonth()) : null)
                .totalAmount(payment.getAmount().getTotalAmount())
                .discountAmount(payment.getAmount().getDiscountAmount())
                .taxFreeAmount(payment.getAmount().getTaxFreeAmount())
                .addressValue(order.getAddress())
                .productInfos(createProductInfoDtos(order.getOrderDetails().getOrderDetailList()))
                .build();
    }

    public static CompleteOrderResponse create(String paymentMethodType, String orderName, String approvedAt, Long totalAmount, Long discountAmount, Long taxFreeAmount, String cardIssuerName, Long cardInstallMonth, Address addressValue, List<ProductInfoDto> productInfos) {
        return CompleteOrderResponse.builder()
                .paymentMethodType(paymentMethodType)
                .orderName(orderName)
                .approvedAt(approvedAt)
                .totalAmount(totalAmount)
                .discountAmount(discountAmount)
                .taxFreeAmount(taxFreeAmount)
                .cardIssuerName(cardIssuerName)
                .cardInstallMonth(cardInstallMonth)
                .addressValue(addressValue)
                .productInfos(productInfos)
                .build();
    }

    // TODO : productInfos -> sellerName,deliveredDate 미완성
    private static List<ProductInfoDto> createProductInfoDtos(List<OrderDetail> orderDetails) {
        return orderDetails.stream()
                .map(o -> ProductInfoDto.of(o.getProduct()))
                .collect(Collectors.toList());
    }
}
