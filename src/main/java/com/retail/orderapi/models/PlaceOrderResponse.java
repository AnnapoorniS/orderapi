package com.retail.orderapi.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class PlaceOrderResponse {

    OrderStatus status;
    String message;
}
