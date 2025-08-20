package sm.order_project.api.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.OneToMany;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Embeddable
public class OrderDetails {

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderDetail> orderDetails = new ArrayList<>();

    public void add(OrderDetail orderDetail) {
        orderDetails.add(orderDetail);
    }

    public List<OrderDetail> getOrderDetailList() {
        return Collections.unmodifiableList(orderDetails);
    }

}
