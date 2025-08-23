package sm.order_project.api.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sm.order_project.api.domain.Product;

@Getter
@NoArgsConstructor
public class ProductInfoDto {

    private String name;
    private Long price;

    @Builder
    private ProductInfoDto(String name, Long price) {
        this.name = name;
        this.price = price;
    }

    // TODO : sellerName 과 deliveredDate 채워넣어야함
    public static ProductInfoDto of(Product product) {
        return ProductInfoDto.builder()
                .name(product.getName())
                .price(product.getPrice())
                .build();
    }

    public static ProductInfoDto create(String name, Long price) {
        return ProductInfoDto.builder()
                .name(name)
                .price(price)
                .build();

    }

}