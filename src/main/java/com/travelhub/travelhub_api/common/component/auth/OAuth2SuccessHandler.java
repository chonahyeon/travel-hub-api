package com.travelhub.travelhub_api.common.component.auth;

import com.travelhub.travelhub_api.data.dto.auth.LoginUserEventDto;
import com.travelhub.travelhub_api.data.dto.auth.OAuthUserDto;
import com.travelhub.travelhub_api.service.auth.JwtService;
import com.travelhub.travelhub_api.service.common.RedisService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Slf4j
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

	private final JwtService jwtService;
	private final RedisService<String, LoginUserEventDto> redisService;

	@Override
	public void onAuthenticationSuccess(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Authentication authentication) {
		OAuthUserDto userInfo = (OAuthUserDto) authentication.getPrincipal();
		jwtService.setIssuedToken(response, userInfo.getUserId(), userInfo.getRole().name(), null);
	}
}
