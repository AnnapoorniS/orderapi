package com.retail.orderapi.services.internal;

import com.retail.orderapi.exceptions.ExpiredTokenException;
import com.retail.orderapi.exceptions.InvalidTokenException;
import com.retail.orderapi.models.UserCredentials;
import com.retail.orderapi.services.TokenService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JWTService implements TokenService {

    private String secret="changeme";

    @Override
    public Claims validateAuthToken(String jwtString) {
        Key signingKey = new SecretKeySpec(Base64.getDecoder().decode(secret), SignatureAlgorithm.HS256.getJcaName());
        return Jwts.parserBuilder().setSigningKey(signingKey).build().parseClaimsJws(jwtString).getBody();
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

        List<GrantedAuthority> grantedAuthorities = AuthorityUtils
                .commaSeparatedStringToAuthorityList("ROLE_USER");

        Key signingKey = new SecretKeySpec(Base64.getDecoder().decode(secret), SignatureAlgorithm.HS256.getJcaName());

        JwtBuilder builder = Jwts.builder();
        builder.claim("authorities",
                grantedAuthorities.stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList()));
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
