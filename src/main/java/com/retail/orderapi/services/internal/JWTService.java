package com.retail.orderapi.services.internal;

import com.retail.orderapi.exceptions.ExpiredTokenException;
import com.retail.orderapi.exceptions.InvalidTokenException;
import com.retail.orderapi.models.UserCredentials;
import com.retail.orderapi.services.TokenService;
import io.jsonwebtoken.*;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;

@Service
public class JWTService implements TokenService {

    @Override
    public Jws<Claims> validateAuthToken(String jwtString, String secret) {
        try {
            Key signingKey = new SecretKeySpec(Base64.getDecoder().decode(secret), SignatureAlgorithm.HS256.getJcaName());

            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(jwtString);
            return claims;
        } catch (ExpiredJwtException ignored) {
            throw new ExpiredTokenException();
        } catch (JwtException ignored) {
            throw new InvalidTokenException();
        }
    }

    @Override
    public String generateAuthToken(UserCredentials userCredentials, String secret, int expirationDays) {
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        Calendar c = Calendar.getInstance();
        c.setTime(now);
        if (expirationDays > 0) {
            c.add(Calendar.DATE, expirationDays);
        }

        Key signingKey = new SecretKeySpec(Base64.getDecoder().decode(secret), SignatureAlgorithm.HS256.getJcaName());

        JwtBuilder builder = Jwts.builder();
        builder.claim("customerUserName", userCredentials.getUserName());
        builder.claim("customerPassword", userCredentials.getPassword());
        builder.setId(userCredentials.getUserName());
        builder.setSubject(userCredentials.getUserName());
        builder.setIssuedAt(now);
        builder.setExpiration(c.getTime());
        builder.signWith(signingKey);
        return builder.compact();
    }
}
