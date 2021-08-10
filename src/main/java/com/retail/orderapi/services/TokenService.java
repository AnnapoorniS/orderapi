package com.retail.orderapi.services;

import com.retail.orderapi.models.UserCredentials;
import io.jsonwebtoken.Claims;

public interface TokenService {
    String generateAuthToken(UserCredentials userCredentials, String secret, int expirationDays);

    Claims validateAuthToken(String jwtString);
}
