package com.travelhub.travelhub_api.service.auth;

import com.travelhub.travelhub_api.data.dto.auth.OAuthUserDTO;
import com.travelhub.travelhub_api.data.enums.common.Role;
import com.travelhub.travelhub_api.data.mysql.entity.common.UserEntity;
import com.travelhub.travelhub_api.data.mysql.repository.common.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

import static com.travelhub.travelhub_api.data.enums.common.Role.ROLE_GUEST;

@Service
@Slf4j
@RequiredArgsConstructor
public class OAuthUserService extends DefaultOAuth2UserService {

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

        String usId = userProfile.get(userNameAttributeName).toString();
        Optional<UserEntity> user = userRepository.findById(usId);

        // db에 저장되어있으면 해당 Role 로 세팅.
        Role usRole = user.map(UserEntity::getUsRole).orElse(ROLE_GUEST);

        OAuthUserDTO loginUserDTO = OAuthUserDTO.builder()
                .name(userNameAttributeName)
                .attribute(userProfile)
                .role(usRole)
                .build();

        user.orElseGet(() -> userRepository.save(loginUserDTO.convert()));
        return loginUserDTO;
    }
}
