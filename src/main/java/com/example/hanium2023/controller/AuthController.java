package com.example.hanium2023.controller;

import com.example.hanium2023.domain.dto.user.GoogleIdTokenDTO;
import com.example.hanium2023.domain.dto.user.UserDto;
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

    @PostMapping("/profile")
    public ResponseEntity<String> updateUserProfile(
            @RequestHeader("Authorization") String token,
            @RequestBody UserDto userDto // 변경된 DTO 사용
    ) {
        try {
            if (jwtTokenValidator.validateToken(token)) {
                // JWT 토큰이 유효한 경우에는 userDto를 사용하여 프로필 정보를 업데이트합니다.
                // authService.updateUserProfile(token, userDto) 또는 다른 서비스 메서드를 호출하여 업데이트할 수 있습니다.
                authService.updateUserProfile(token, userDto);
                // 예를 들어, 업데이트가 성공했을 때
                return ResponseEntity.ok("프로필이 업데이트되었습니다.");
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }

    /*@GetMapping("/profile")
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
    }*/
}
