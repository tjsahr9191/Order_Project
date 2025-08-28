package sm.order_project.api.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class
Delivery extends BaseEntity{

    @Id @Column(name = "delivery_id")
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Setter
    @Embedded
    private Address address;

    @Enumerated(EnumType.STRING)
    private DeliveryStatus deliveryStatus;

    @Builder
    public Delivery(Address address, DeliveryStatus deliveryStatus) {
        this.address = address;
        this.deliveryStatus = deliveryStatus;
    }

}