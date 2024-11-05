package com.travelhub.travelhub_api.common.component.client;

import com.travelhub.travelhub_api.data.request.TokenRequest;
import com.travelhub.travelhub_api.data.response.TokenResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;

@FeignClient(value = "googleOAuthClient", url = "https://oauth2.googleapis.com")
public interface GoogleOAuthClient {

    /*
     * POST /token
     * oauth 인증용 토큰 발급
     */
    @PostMapping(value = "/token", consumes = APPLICATION_FORM_URLENCODED_VALUE)
    TokenResponse getOAuthToken(TokenRequest tokenRequest);
}
