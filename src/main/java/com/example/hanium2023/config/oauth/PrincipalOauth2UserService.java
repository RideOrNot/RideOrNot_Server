package com.example.hanium2023.config.oauth;

import ch.qos.logback.core.net.SyslogOutputStream;
//import com.example.hanium2023.config.auth.PrincipalDetails;
import com.example.hanium2023.domain.entity.User;
import com.example.hanium2023.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {
/*
    //@Autowired
    //private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private UserRepository userRepository;

    //구글로 부터 받은 유저리퀘스트 데이터에 대한 후처리 되는 함수
    @Override //유저리퀘스트에 엑세스토큰+사용자 정보 까지 내용이 리턴된다.
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        System.out.println("getClientRegistration :" + userRequest.getClientRegistration());
        System.out.println("getAccessToken :" + userRequest.getAccessToken().getTokenValue());
        // 구글로그인 버튼을 클릭하면 로그인 창이 뜨고 로그인을 하면 코드를 리턴 받고 oauth 클라이언트 라이브러리가 받고 엑세스 토큰 요청
        // 유저리퀘스트 정보로 회원 프로필을 받아와야함(loadUser함수를 통해서 구글로부터 회원 프로필을 받아준다.)
        System.out.println("getAttributes :" + super.loadUser(userRequest).getAttributes());

        OAuth2User oauth2User = super.loadUser(userRequest);
        System.out.println("getAttributes :" + oauth2User.getAttributes());

        String userId = oauth2User.getAttribute("sub");
        String nickname = oauth2User.getName();
        String provider = userRequest.getClientRegistration().getClientId(); //google
        String providerId = oauth2User.getAttribute("sub");
        String username = provider+"_"+providerId; //google_123123123
        String email = oauth2User.getAttribute("email");
        String password = userRequest.getClientRegistration().getClientId();
        String role = "ROLE_USER";

        User userEntity = userRepository.findByUsername(username);

        if(userEntity == null){
            userEntity = User.builder()
                    .userId(userId)
                    .nickname(nickname)
                    .password(password)
                    .username(username)
                    .email(email)
                    .role(role)
                    .provider(provider)
                    .providerId(providerId)
                    .build();
            userRepository.save(userEntity);
            System.out.println("신규회원가입이 됨");

        }else{
            System.out.println("원래 유저입니다.");
        }
        return new PrincipalDetails(userEntity, oauth2User.getAttributes());
    }*/
}
