package com.travelhub.travelhub_api.common.component.auth;

import com.travelhub.travelhub_api.common.resource.exception.AuthException;
import com.travelhub.travelhub_api.data.dto.auth.OAuthUserDTO;
import com.travelhub.travelhub_api.data.enums.common.Role;
import com.travelhub.travelhub_api.data.mysql.entity.common.UserEntity;
import com.travelhub.travelhub_api.data.mysql.repository.common.UserRepository;
import com.travelhub.travelhub_api.service.auth.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.travelhub.travelhub_api.common.resource.TravelHubResource.*;
import static com.travelhub.travelhub_api.data.enums.common.ErrorCodes.TOKEN_INVALID;
import static com.travelhub.travelhub_api.data.enums.common.Role.ROLE_GUEST;

@RequiredArgsConstructor
@Component
@Slf4j
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

	private final JwtService jwtService;
	private final UserRepository userRepository;
	private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();


	@Override
	public void onAuthenticationSuccess(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Authentication authentication) throws IOException {
		OAuthUserDTO oAuthUserDTO = (OAuthUserDTO) authentication.getPrincipal();
		String usId = oAuthUserDTO.getUserId();

		UserEntity userEntity = userRepository.findById(usId)
				.orElseThrow(() -> new AuthException(TOKEN_INVALID));

		String redirectURI;
		Role usRole = userEntity.getUsRole();

		/*
		 * role 이 guest 인 경우는 회원가입을 하지않은 신규 유저이기 때문에 회원 가입 진행
		 * 그 외에는 토큰 발급 진행
		 */
		if (ROLE_GUEST == usRole) {
			HttpSession session = request.getSession();
			session.setAttribute("usId", usId);
			redirectURI = API_V1_AUTH + AUTH_SIGNUP;
		} else {
			jwtService.setIssuedToken(response, oAuthUserDTO.getUserId(), oAuthUserDTO.getRole().name());
			redirectURI = HOME;
		}

		redirectStrategy.sendRedirect(request, response, redirectURI);
	}
}
