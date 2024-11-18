package com.travelhub.travelhub_api.service.oauth;

import java.util.Map;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.travelhub.travelhub_api.data.dto.UserSessionDto;
import com.travelhub.travelhub_api.data.enums.Role;
import com.travelhub.travelhub_api.data.mysql.entity.User;
import com.travelhub.travelhub_api.data.mysql.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomOAuthService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // 서비스를 구분하는 코드 ex) Github, Naver
        String providerCode = userRequest.getClientRegistration()
                .getRegistrationId();

        // user 별 고유 값을 담고 있는 key 값
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName();

        // user profile
        Map<String, Object> userProfile = oAuth2User.getAttributes();
        log.info("login code : {}, user info : {}", providerCode, userProfile);

        UserSessionDto userSession = UserSessionDto.builder()
                .name(userNameAttributeName)
                .attribute(userProfile)
                .role(Role.ROLE_USER)
                .build();

        User saveUser = userSession.convert();
        userRepository.save(saveUser);

        // Security context에 저장할 객체 생성
        return userSession;
    }
}