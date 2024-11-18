package com.travelhub.travelhub_api.common.component;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.travelhub.travelhub_api.data.dto.UserSessionDto;
import com.travelhub.travelhub_api.service.oauth.JwtService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

	private final JwtService jwtService;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
		UserSessionDto userInfo = (UserSessionDto) authentication.getPrincipal();
		String token = jwtService.createToken(userInfo);
		
		// token 정보 넣어주기
	}
}
