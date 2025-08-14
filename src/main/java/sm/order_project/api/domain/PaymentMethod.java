package sm.order_project.api.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentMethod {

    CARD("카드"),
    MONEY("돈");

    private final String text;

}
