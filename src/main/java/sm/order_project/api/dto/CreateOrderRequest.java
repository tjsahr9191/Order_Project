package sm.order_project.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sm.order_project.api.kakao.KakaoPayReadyRequest;

import java.util.List;
import java.util.stream.Collectors;


@Getter
@NoArgsConstructor
public class CreateOrderRequest {

    @NotNull
    private Long totalAmount;

    @NotNull
    private String orderName;

    @NotNull
    @Valid
    private List<CreateOrderRequest.ProductInfo> productValues;

    @Builder
    public CreateOrderRequest(Long totalAmount, String orderName, List<CreateOrderRequest.ProductInfo> productValues) {
        this.totalAmount = totalAmount;
        this.orderName = orderName;
        this.productValues = productValues;
    }


    public CreateOrderDto toDto(String orderNo, String tid, Long memberId) {
        return CreateOrderDto.builder()
                .orderNo(orderNo)
                .memberId(memberId)
                .tid(tid)
                .orderName(orderName)
                .totalOrderPrice(totalAmount)
                .productValues(productValues.stream().map(ProductInfo::toDto).collect(Collectors.toList()))
                .build();
    }

    public KakaoPayReadyRequest toKakaoReadyRequest(String orderNo, Long memberId, String cid, String approvalUrl, String failUrl, String cancelUrl) {
        return KakaoPayReadyRequest.builder()
                .partnerOrderId(orderNo)
                .partnerUserId(memberId.toString())
                .cid(cid)
                .approvalUrl(approvalUrl)
                .failUrl(failUrl)
                .cancelUrl(cancelUrl)
                .quantity(productValues.stream().mapToLong(ProductInfo::getQuantity).sum())
                .itemName(orderName)
                .totalAmount(totalAmount)
                .taxFreeAmount(0L)
                .build();
    }

    @Getter
    @NoArgsConstructor
    public static class ProductInfo{

        @NotNull
        Long productId;
        @NotNull
        Long price;
        @NotNull
        Long quantity;

        @Builder
        private ProductInfo(Long price, Long quantity) {
            this.price = price;
            this.quantity = quantity;
        }

        public CreateOrderDto.ProductDto toDto() {
            return CreateOrderDto.ProductDto.builder()
                    .productId(productId)
                    .price(price)
                    .quantity(quantity)
                    .build();
        }
    }

}
