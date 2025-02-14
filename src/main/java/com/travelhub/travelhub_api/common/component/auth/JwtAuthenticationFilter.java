package com.travelhub.travelhub_api.common.component.auth;

import com.travelhub.travelhub_api.common.util.CookieUtil;
import com.travelhub.travelhub_api.service.auth.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;

import static com.travelhub.travelhub_api.common.resource.TravelHubResource.*;
import static com.travelhub.travelhub_api.data.enums.common.Role.ROLE_USER;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    /*
     * 401일때 어떻게 할지..
     */
    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        String[] excludePath = {
                AUTH_LOGIN,
                API_V1_AUTH + AUTH_SIGNUP,
                API_V1_IMAGE
        };
        String path = request.getRequestURI();
        return Arrays.stream(excludePath).anyMatch(path::startsWith);
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        Cookie[] cookies = request.getCookies();

        // 쿠키에 토큰이 있으면 복호화 진행. 없으면 oauth 로그인.
        if (Objects.nonNull(cookies)) {
            Optional<String> accessToken = CookieUtil.findCookie(cookies, AUTH_ACCESS_TOKEN);

            // 유효한 토큰일때만 인증정보 세팅.
            if (accessToken.isPresent()) {
                String token = accessToken.get();
                Claims claims = jwtService.parseToken(token, false);

                String usId = claims.get("usId").toString();
                jwtService.validUser(usId, token, false);

                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        usId,
                        null,
                        Collections.singletonList(new SimpleGrantedAuthority(ROLE_USER.name()))
                );
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}
