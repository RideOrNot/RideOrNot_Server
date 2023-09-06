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

    // 추가적인 API 엔드포인트를 만들어서 검증된 JWT 토큰을 사용할 수 있습니다.
    @GetMapping("/profile")
    public ResponseEntity<String> getUserProfile(@RequestHeader("Authorization") String token) {
        try {
            // JWT 토큰을 검증하고 유효한 경우에만 사용자 프로필을 반환합니다.
            if (jwtTokenValidator.validateToken(token)) {
                // JWT 토큰이 유효한 경우에는 사용자 프로필 정보를 반환하거나 다른 작업을 수행할 수 있습니다.
                // 사용자 프로필을 가져오는 서비스 메서드를 호출하고 그 결과를 반환하면 됩니다.
                // 예를 들어, authService.getUserProfile(token) 등의 메서드를 호출하여 사용자 정보를 가져오세요.
                // 이 예제에서는 간단하게 "Hello, user!" 메시지를 반환하겠습니다.
                return ResponseEntity.ok("Hello, user!");
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