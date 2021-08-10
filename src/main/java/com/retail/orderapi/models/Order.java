package com.retail.orderapi.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Getter
@Setter
@Document
@JsonIgnoreProperties(ignoreUnknown = true)
public class Order implements Serializable {
    @Id
    private String orderId;
    @NotNull
    private String customerUserName;
    private OrderStatus status;
    @NotNull
    private String orderDate;
    private PaymentMode paymentMode;
    @NotNull
    private String shippingAddress;
    private Item[] orderedItems;
    private long itemsTotalPrice;
    private long packingCost;
    private long tax;
    private long total;

    @java.lang.Override
    public java.lang.String toString() {
        return "Order{" +
                "orderId='" + orderId + '\'' +
                ", customerUserName='" + customerUserName + '\'' +
                ", status=" + status +
                ", orderDate='" + orderDate + '\'' +
                ", paymentMode=" + paymentMode +
                ", shippingAddress='" + shippingAddress + '\'' +
                ", orderedItems=" + java.util.Arrays.toString(orderedItems) +
                ", itemsTotalPrice=" + itemsTotalPrice +
                ", packingCost=" + packingCost +
                ", tax=" + tax +
                ", total=" + total +
                '}';
    }
}
