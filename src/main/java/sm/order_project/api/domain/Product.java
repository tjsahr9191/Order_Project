package sm.order_project.api.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    private String name;

    private Long price;

    private Long stock;

    private String productNo;

    @Builder
    public Product(String name, Long price, Long stock, String productNo) {
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.productNo = productNo;
    }

}
