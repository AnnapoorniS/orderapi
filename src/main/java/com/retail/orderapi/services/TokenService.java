package com.retail.orderapi.services;

import com.retail.orderapi.models.UserCredentials;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;

public interface TokenService {
    String generateAuthToken(UserCredentials userCredentials, String secret, int expirationDays);

    Jws<Claims> validateAuthToken(String jwtString, String secret);
}
