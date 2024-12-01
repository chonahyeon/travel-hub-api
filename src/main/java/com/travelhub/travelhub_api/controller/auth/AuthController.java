package com.travelhub.travelhub_api.controller.auth;

import com.travelhub.travelhub_api.controller.auth.request.SignUpRequest;
import com.travelhub.travelhub_api.data.dto.auth.LoginUserDTO;
import com.travelhub.travelhub_api.service.auth.JwtService;
import com.travelhub.travelhub_api.service.auth.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import static com.travelhub.travelhub_api.common.resource.TravelHubResource.*;

@Controller
@RequestMapping(API_V1_AUTH)
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;

    /**
     * api 토큰 갱신
     * /travel/v1/auth/renew
     */
    @PutMapping(AUTH_RENEW_TOKEN)
    public void renewAccessToken(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        jwtService.renewToken(response, cookies);
    }

    /**
     * 로그아웃 처리
     * /travel/v1/auth/logout
     */
    @GetMapping(AUTH_LOGOUT)
    public String logout(HttpServletResponse response) {
        jwtService.invalidateToken(response, LoginUserDTO.get());
        return REDIRECT_PREFIX + HOME;
    }

    /*
     * 회원 가입 Form
     */
    @GetMapping(AUTH_SIGNUP)
    public String signUpForm() {
        return "sign-up";
    }

    /**
     * 회원 가입
     * /travel/v1/auth/signup
     */
    @PostMapping(AUTH_SIGNUP)
    public String signUp(HttpSession httpSession, @ModelAttribute("signUpInfo") @Valid SignUpRequest request, BindingResult bindingResult, Model model) {
        String usId = (String) httpSession.getAttribute("usId");

        // 유효성 검사 실패
        if (bindingResult.hasErrors()) {
            model.addAttribute("signUpInfo", request);
            return "sign-up";
        }

        userService.signUp(usId, request);
        return REDIRECT_PREFIX + AUTH_LOGIN;
    }
}
