package com.travelhub.travelhub_api.common.component.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.travelhub.travelhub_api.common.resource.exception.AuthException;
import com.travelhub.travelhub_api.data.enums.common.ErrorCodes;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtExceptionHandlingFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /*
     * JwtAuthentication Filter 에서 발생한 에러 핸들링
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            log.error("doFilterInternal() : 인증 오류", e);
            setResponse(response, e);
        }
    }

    private void setResponse(HttpServletResponse response, Exception e) throws IOException {
        ErrorCodes codes = null;

        // 토큰 만료를 제외한 JWT 에러 일괄 처리
        if (e instanceof ExpiredJwtException) {
            codes = ErrorCodes.TOKEN_EXPIRE;
        } else if (e instanceof SecurityException || e instanceof JwtException || e instanceof AuthException) {
            codes = ErrorCodes.TOKEN_INVALID;
        }

        response.setStatus(UNAUTHORIZED.value());
        response.getWriter().write(objectMapper.writeValueAsString(codes));
    }
}
