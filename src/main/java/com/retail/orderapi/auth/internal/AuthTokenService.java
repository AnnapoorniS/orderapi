package com.retail.orderapi.auth.internal;

import com.retail.orderapi.auth.LoginService;
import com.retail.orderapi.models.UserCredentials;
import com.retail.orderapi.models.UserTokenResponse;
import com.retail.orderapi.services.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AuthTokenService implements LoginService {

    @Value("${user.auth.secret}")
    private String secret;

    @Value("${user.auth.expireInDays}")
    private int expireInDays;

    @Autowired
    TokenService tokenService;

    @Override
    public UserTokenResponse loginUser(UserCredentials userCredentials) {
        UserTokenResponse userTokenResponse = new UserTokenResponse();
        userTokenResponse.setUserName(userCredentials.getUserName());
        String token = tokenService.generateAuthToken(userCredentials, secret, expireInDays);
        userTokenResponse.setToken("Bearer " + token);
        return userTokenResponse;
    }
}
