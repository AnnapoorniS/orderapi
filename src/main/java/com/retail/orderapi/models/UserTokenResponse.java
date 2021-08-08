package com.retail.orderapi.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserTokenResponse implements Serializable {
    @NotNull
    private String userName;
    @NotNull
    private String token;

}
