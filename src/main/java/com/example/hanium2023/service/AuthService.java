package com.example.hanium2023.service;

import com.example.hanium2023.domain.entity.User;
import com.example.hanium2023.repository.UserRepository;
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
    UserRepository userRepository;

    public void verifyGoogleIdToken(String googleIdToken) throws GeneralSecurityException, IOException {
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
                String id = (String) payload.get("sub");

                User newUser = new User();
                //newUser.setEmail(email);
                newUser = User.builder()
                        .userId(id)
                        .nickname(firstName)
                        .username(fullName)
                        .email(email)
                        .success(true)
                        .build();
                userRepository.save(newUser);
                System.out.println("신규 유저 처리 완료");
            }
        } else {
        System.out.println("Invalid ID token.");
    }


        /*if (idToken != null) {
            GoogleIdToken.Payload payload = idToken.getPayload();

            // Print user identifier
            String userId = payload.getSubject();
            System.out.println("User ID: " + userId);

            // Get profile information from payload
            String email = payload.getEmail();
            boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());
            String name = (String) payload.get("name");
            String pictureUrl = (String) payload.get("picture");
            String locale = (String) payload.get("locale");
            String familyName = (String) payload.get("family_name");
            String givenName = (String) payload.get("given_name");

            // Use or store profile information
            // ...

        } else {
            System.out.println("Invalid ID token.");
        }*/

        }

    /*@Autowired
    private UserRepository userRepository;

    //GoogleIdTokenVerifier를 사용하여 토큰을 검증
    public void verifyGoogleIdToken(String googleIdToken) throws VerificationException {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                .setAudience(Collections.singletonList(CLIENT_ID))
                .build();
        try {
            GoogleIdToken idToken = verifier.verify(googleIdToken);
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
                    String id = (String) payload.get("sub");

                    User newUser = new User();
                    //newUser.setEmail(email);
                    newUser = User.builder()
                            .userId(id)
                            .nickname(firstName)
                            .username(fullName)
                            .email(email)
                            .build();
                    userRepository.save(newUser);
                    System.out.println("신규 유저 처리 완료");
                }
            } else {
                throw new VerificationException("Token verification failed");
            }
        } catch (GeneralSecurityException | IOException e) {
            throw new VerificationException("Token verification error", e);
        }
    }*/

}

