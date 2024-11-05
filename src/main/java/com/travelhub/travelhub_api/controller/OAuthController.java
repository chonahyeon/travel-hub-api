package com.travelhub.travelhub_api.controller;

import com.travelhub.travelhub_api.service.OAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/login/oauth2")
@RequiredArgsConstructor
public class OAuthController {

    private final OAuthService oAuthService;

    /**
     * 인증 코드 발급
     * @param code authorization code
     */
    @GetMapping("/code")
    public void getAuthorizationCode(@RequestParam(name = "code") String code) {
        log.info("진입 : {}", code);
        oAuthService.getAccessToken(code);
    }

    @GetMapping("/token")
    public void issueAccessToken() {
        log.info("토큰 발급 완료 진입");
    }
}
