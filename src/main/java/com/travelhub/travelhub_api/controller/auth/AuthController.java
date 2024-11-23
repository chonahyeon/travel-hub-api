package com.travelhub.travelhub_api.controller.auth;

import com.travelhub.travelhub_api.data.dto.auth.LoginUser;
import com.travelhub.travelhub_api.service.auth.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/travel/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtService jwtService;

    /**
     * api 토큰 갱신
     * /travel/v1/auth/renew
     */
    @PutMapping("/renew")
    public void renewAccessToken(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        jwtService.renewToken(response, cookies);
    }

    /**
     * 로그아웃 처리
     * /travel/v1/auth/logout
     */
    @PutMapping("/logout")
    public void logout() {
        jwtService.invalidateToken(LoginUser.get());
    }
}
