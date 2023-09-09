package com.example.hanium2023.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtTokenValidator {

    @Value("${jwt.secret}")
    private String secret;

    public boolean validateToken(String token) {
        try {
            System.out.println("token : " + token);
            //SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes()); // X
            // 토큰의 클레임(claims)을 파싱합니다.
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secret)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String email = claims.get("email", String.class);
            System.out.println("이메일 추출해보기 " + email);
            Date expirationDate = claims.getExpiration();
            Date now = new Date();

            System.out.println("JWT 토큰 검증 성공 true 반환");
            return expirationDate.after(now);
        } catch (Exception e) {
            System.out.println("JWT 토큰 검증 실패 false 반환"); //JWT 토큰 검증 실패
            return false;
        }
    }
}
