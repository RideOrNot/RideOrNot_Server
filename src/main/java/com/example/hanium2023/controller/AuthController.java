package com.example.hanium2023.controller;

import com.example.hanium2023.domain.dto.user.GoogleIdTokenDTO;
import com.example.hanium2023.service.AuthService;
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

    @PostMapping("/signin")
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
    }
}


/*@RestController
public class AuthController { //클라이언트의 POST 요청을 받아 구글 아이디 토큰을 검증

    @Autowired
    private AuthService authService;

    @RequestMapping(value = "/logingoogletoken", method = RequestMethod.POST) //@PostMapping("/login") // /login 엔드포인트에서 클라이언트로부터 받은 토큰을 검증하고 결과를 응답
    public ResponseEntity<String> login(String googleIdToken) {
        try { //검증 성공 시 해당 토큰의 정보를 이용하여 유저 정보 처리 로직을 실행
            authService.verifyGoogleIdToken(googleIdToken);
            //유저 정보 처리 로직
            return ResponseEntity.ok("Token verification successful");
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}*/
