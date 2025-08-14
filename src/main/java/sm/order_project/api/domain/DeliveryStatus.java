package sm.order_project.api.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DeliveryStatus {

    ORDER("주문"),
    CANCELLED("취소");

    private final String text;

}