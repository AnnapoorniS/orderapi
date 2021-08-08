package com.retail.orderapi.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document
@JsonIgnoreProperties(ignoreUnknown = true)
public class Item {
    private int quantity;
    private int productId;
    private String productName;
    private String productPrice;
}
