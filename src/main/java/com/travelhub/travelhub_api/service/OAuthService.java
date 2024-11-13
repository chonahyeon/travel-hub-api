package com.travelhub.travelhub_api.service;

import com.travelhub.travelhub_api.common.component.client.GoogleOAuthClient;
import com.travelhub.travelhub_api.common.component.client.GoogleUtilClient;
import com.travelhub.travelhub_api.data.request.TokenRequest;
import com.travelhub.travelhub_api.data.response.TokenResponse;
import com.travelhub.travelhub_api.data.response.UserInfoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class OAuthService {

    private final GoogleOAuthClient googleOAuthClient;
    private final GoogleUtilClient googleUtilClient;

    /**
     * 코드 발급이 완료됬으면 해당 코드로 토큰 발급 진행
     * @param authorizationCode 코드
     */
    public void getAccessToken(String authorizationCode) {
        // 토큰 발급 요청
        TokenRequest request = TokenRequest.ofAccessToken(authorizationCode);
        TokenResponse response = googleOAuthClient.getOAuthToken(request);
        log.info("{}", response);

        String accessToken = response.getAccessToken();
        getUserInfo(accessToken);
    }

    /**
     * 발급된 AccessToken 으로 사용자 정보 가져오기
     */
    public void getUserInfo(String accessToken) {
        UserInfoResponse userResources = googleUtilClient.getUserResources(accessToken);
        log.info("{}", userResources);
    }
}
