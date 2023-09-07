package com.example.hanium2023.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtTokenValidator {

    private final SecretKey secretKey;
    private final long validityInMilliseconds;

    public JwtTokenValidator(@Value("${jwt.secret}") String secret, @Value("${jwt.validity}") long validityInMilliseconds) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
        this.validityInMilliseconds = validityInMilliseconds;
    }

    public boolean validateToken(String token) {
        try {
            System.out.println("token : " + token);
            Claims claims = Jwts.parserBuilder() //Excaption 발생하는 듯
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            Date expirationDate = claims.getExpiration();
            Date now = new Date();

            return expirationDate.after(now);
        } catch (Exception e) {
            // JWT 토큰 검증 실패
            return false;
        }
    }
}
