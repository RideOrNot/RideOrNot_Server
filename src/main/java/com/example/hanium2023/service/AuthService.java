package com.example.hanium2023.service;

import com.example.hanium2023.domain.entity.User;
import com.example.hanium2023.repository.UserRepository;
import com.example.hanium2023.util.JwtTokenProvider;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Service
public class AuthService { //클라이언트로부터 받은 구글 아이디 토큰을 검증하고 유저 정보를 확인 및 생성하는 비즈니스 로직

    private final String CLIENT_ID = "900575659421-q7u2890lr94ik4o440mqmi1stj7sm6ik.apps.googleusercontent.com"; // 구글 클라이언트 ID
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider; // JwtTokenProvider 추가

    @Autowired
    public AuthService(UserRepository userRepository, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider; // JwtTokenProvider 주입

    }

    public String verifyGoogleIdToken(String googleIdToken) throws GeneralSecurityException, IOException {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                    .setAudience(Collections.singletonList(CLIENT_ID))
                    .build();

            GoogleIdToken idToken = verifier.verify(googleIdToken);
            System.out.println(idToken);

            if (idToken != null) { //true){
                // 검증된 토큰의 payload에서 이메일 정보를 추출
                GoogleIdToken.Payload payload = idToken.getPayload(); //nullPointException
                String email = payload.getEmail();

                // UserRepository를 사용하여 DB에서 해당 이메일의 유저 정보를 조회
                User existingUser = userRepository.findByEmail(email);
                String jwtToken = jwtTokenProvider.createToken(email);// JwtTokenProvider를 사용하여 JWT 토큰 생성
                if (existingUser != null) {
                    // 이미 등록된 유저 처리 로직
                    System.out.println("JWT 토큰: " + jwtToken);
                    System.out.println("이미 등록된 유저");
                    return jwtToken;
                } else {
                    // 신규 유저 처리 로직
                    String fullName = (String) payload.get("name");
                    String firstName = (String) payload.get("given_name");

                    User newUser = User.builder()
                            .nickname(firstName)
                            .username(fullName)
                            .email(email)
                            .build();
                    userRepository.save(newUser);

                    // JWT 토큰 생성 구글 토큰 정보 추출해야 사용 가능

                    System.out.println("JWT 토큰: " + jwtToken);
                    System.out.println("신규 유저 처리 완료");
                    return jwtToken; // JWT 토큰 반환
                }
            } else {
                System.out.println("Invalid ID token.");
                return null;
            }
        } catch (GeneralSecurityException | IOException e) {
            System.out.println("Token verification failed: " + e.getMessage());
            return null;
        }
    }
}


    /*public void verifyGoogleIdToken(String googleIdToken) throws GeneralSecurityException, IOException {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                // Specify the CLIENT_ID of the app that accesses the backend:
                .setAudience(Collections.singletonList(CLIENT_ID))
                // Or, if multiple clients access the backend:
                //.setAudience(Arrays.asList(CLIENT_ID_1, CLIENT_ID_2, CLIENT_ID_3))
                .build();

        GoogleIdToken idToken = verifier.verify(googleIdToken);
        System.out.println(idToken);

        if (idToken != null) { //검증된 토큰의 payload에서 이메일 정보를 추출
            GoogleIdToken.Payload payload = idToken.getPayload();
            String email = payload.getEmail();

            //UserRepository를 사용하여 DB에서 해당 이메일의 유저 정보를 조회
            User existingUser = userRepository.findByEmail(email);
            if (existingUser != null) {
                // 이미 등록된 유저 처리 로직
                System.out.println("이미 등록된 유저");
            } else {
                // 신규 유저 처리 로직
                String fullName = (String) payload.get("name");
                String firstName = (String) payload.get("given_name");
//                String id = (String) payload.get("sub");

                //User newUser = new User();
                User newUser;
                //newUser.setEmail(email);
                newUser = User.builder()
//                        .userId(id)
                        .nickname(firstName)
                        .username(fullName)
                        .email(email)
//                        .success(true)
                        .build();
                userRepository.save(newUser);
                System.out.println("신규 유저 처리 완료");
            }
        } else {
            System.out.println("Invalid ID token.");
        }
    }*/

