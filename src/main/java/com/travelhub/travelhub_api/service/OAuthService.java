package com.travelhub.travelhub_api.service;

import com.travelhub.travelhub_api.common.component.client.GoogleOAuthClient;
import com.travelhub.travelhub_api.data.request.TokenRequest;
import com.travelhub.travelhub_api.data.response.TokenResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class OAuthService {

    private final GoogleOAuthClient googleOAuthClient;

    /**
     * 코드 발급이 완료됬으면 해당 코드로 토큰 발급 진행
     * @param authorizationCode 코드
     */
    public void getAccessToken(String authorizationCode) {
        TokenRequest request = TokenRequest.ofAccessToken(authorizationCode);
        log.info("{}", request);

        TokenResponse response = googleOAuthClient.getOAuthToken(request);
        log.info("{}", response);
    }
}
