package com.example.hanium2023.controller;

import com.example.hanium2023.domain.dto.user.GoogleIdTokenDTO;
import com.example.hanium2023.service.AuthService;
import com.example.hanium2023.service.JwtTokenValidator;
import com.example.hanium2023.service.VerificationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;

@RestController
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtTokenValidator jwtTokenValidator; // JwtTokenValidator 주입

    @PostMapping("/signIn")
    public ResponseEntity<String> login(@RequestBody GoogleIdTokenDTO dto) {
        try {
            String googleIdToken = dto.getGoogleIdToken();
            String jwtToken = authService.verifyGoogleIdToken(googleIdToken);

            if (jwtToken != null) {
                // JWT 토큰을 응답 바디에 담아서 반환
                return ResponseEntity.ok(jwtToken);
            } else {
                // 토큰 검증 실패 시 에러 응답
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token verification failed");
            }
        } catch (GeneralSecurityException | IOException e) {
            // 예외 발생 시 에러 응답
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<String> getUserProfile(@RequestHeader("Authorization") String token) {
        try {
            if (jwtTokenValidator.validateToken(token)) {
                return ResponseEntity.ok("jwt 유효");
            } else {
                // 토큰이 유효하지 않은 경우에는 UNAUTHORIZED 상태 코드를 반환합니다.
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
            }
        } catch (Exception e) {
            // 예외 발생 시 에러 응답
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }
}




    /*private final JwtTokenValidator jwtTokenValidator;
    private final AuthService authService;

    @Autowired
    public AuthController(JwtTokenValidator jwtTokenValidator, AuthService authService) {
        this.jwtTokenValidator = jwtTokenValidator;
        this.authService = authService;
    }

    @PostMapping("/signIn")
    public ResponseEntity<String> login(@RequestBody GoogleIdTokenDTO dto) {
        try {
            String googleIdToken = dto.getGoogleIdToken();

            // JWT 토큰 검증
            if (jwtTokenValidator.validateToken(googleIdToken)) {
                String jwtToken = authService.verifyGoogleIdToken(googleIdToken);
                if (jwtToken != null) {
                    // JWT 토큰을 응답 바디에 담아서 반환
                    return ResponseEntity.ok(jwtToken);
                }
            }

            // 토큰 검증 실패 또는 다른 오류 발생 시 에러 응답
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token verification failed");
        } catch (GeneralSecurityException | IOException e) {
            // 예외 발생 시 에러 응답
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }*/

    /*@Autowired
    private AuthService authService;

    @PostMapping("/signIn")
    public ResponseEntity<String> login(@RequestBody GoogleIdTokenDTO dto) {
        try {
            String googleIdToken = dto.getGoogleIdToken();
            String jwtToken = authService.verifyGoogleIdToken(googleIdToken);
            if (jwtToken != null) {
                // JWT 토큰을 응답 바디에 담아서 반환
                return ResponseEntity.ok(jwtToken);
            } else {
                // 토큰 검증 실패 또는 다른 오류 발생 시 에러 응답
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token verification failed");
            }
        } catch (GeneralSecurityException | IOException e) {
            // 예외 발생 시 에러 응답
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }*/