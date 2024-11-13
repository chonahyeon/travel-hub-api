package com.travelhub.travelhub_api.controller;

import com.travelhub.travelhub_api.service.OAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/login/oauth2")
@RequiredArgsConstructor
public class OAuthController {

    private final OAuthService oAuthService;

    /**
     * 인증 코드 발급
     * oauth 인증 동의 완료 후 진입.
     * @param code authorization code
     */
    @GetMapping("/code")
    public void getAuthorizationCode(@RequestParam(name = "code") String code) {
        oAuthService.getAccessToken(code);
    }

    /**
     *
     */
    @PostMapping("/token")
    public void issueAccessToken(@RequestBody Map<String,Object> param) {
        log.info("토큰 발급 완료 진입. {}", param);
    }
}
