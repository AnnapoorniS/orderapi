package com.retail.orderapi.auth;

import com.retail.orderapi.models.UserCredentials;
import com.retail.orderapi.models.UserTokenResponse;

public interface LoginService {
    UserTokenResponse loginUser(UserCredentials userCredentials);
}
