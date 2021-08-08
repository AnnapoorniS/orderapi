package com.retail.orderapi.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Getter
@Setter
@Document
@JsonIgnoreProperties(ignoreUnknown = true)
public class Customer implements Serializable {
    private String firstName;
    private String lastName;
    @Indexed(unique = true)
    @NotNull
    private String userName;
    @NotNull
    private String password;
    private String shippingAddress;
    private long contactNumber;

}
