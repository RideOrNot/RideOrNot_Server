package com.example.hanium2023.controller;

import com.example.hanium2023.service.AuthService;
import com.example.hanium2023.service.VerificationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class AuthController { //클라이언트의 POST 요청을 받아 구글 아이디 토큰을 검증

    @Autowired
    private AuthService authService;

    @PostMapping("/login") // /login 엔드포인트에서 클라이언트로부터 받은 토큰을 검증하고 결과를 응답
    public ResponseEntity<String> login(@RequestBody String googleIdToken) {
        try { //검증 성공 시 해당 토큰의 정보를 이용하여 유저 정보 처리 로직을 실행
            authService.verifyGoogleIdToken(googleIdToken);
            // TODO: 유저 정보 처리 로직
            return ResponseEntity.ok("Token verification successful");
        } catch (VerificationException e) {
            return ResponseEntity.badRequest().body("Token verification failed");
        }
    }
}
