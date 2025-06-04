package com.travelhub.travelhub_api.common.component.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.travelhub.travelhub_api.common.resource.exception.AuthException;
import com.travelhub.travelhub_api.data.enums.common.ResponseCodes;
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

import static com.travelhub.travelhub_api.data.enums.common.ResponseCodes.*;

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
        } catch (ExpiredJwtException e) {
            log.error("doFilterInternal() : JWT 만료", e);
            setResponse(response, TOKEN_EXPIRE);
        } catch (SecurityException | JwtException e) {
            log.error("doFilterInternal() : JWT 인증 오류", e);
            setResponse(response, TOKEN_INVALID);
        } catch (AuthException e) {
            log.error("doFilterInternal() : JWT 인증 오류", e);
            setResponse(response, e.getResponseCodes());
        } catch (Exception e) {
            log.error("doFilterInternal() : 내부 오류", e);
            setResponse(response, SERVER_ERROR);
        }
    }

    /**
     * 인증 에러별 응답 공통화
     * @param response servlet response
     * @param codes 에러코드
     * @throws IOException
     */
    private void setResponse(HttpServletResponse response, ResponseCodes codes) throws IOException {
        response.setStatus(codes.getStatus().value());
        response.getWriter().write(objectMapper.writeValueAsString(codes));
    }
}
