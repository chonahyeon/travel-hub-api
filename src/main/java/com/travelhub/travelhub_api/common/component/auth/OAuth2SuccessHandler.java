package com.travelhub.travelhub_api.common.component.auth;

import com.travelhub.travelhub_api.common.resource.exception.AuthException;
import com.travelhub.travelhub_api.data.dto.auth.OAuthUserDTO;
import com.travelhub.travelhub_api.data.enums.common.Role;
import com.travelhub.travelhub_api.data.mysql.entity.common.User;
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
		OAuthUserDTO userInfo = (OAuthUserDTO) authentication.getPrincipal();

		String usId = userInfo.getUserId();
		User user = userRepository.findById(usId).orElseThrow(AuthException::new);
		Role usRole = user.getUsRole();

		String redirectURI;
		if (ROLE_GUEST == usRole) {
			HttpSession session = request.getSession();
			session.setAttribute("usId", usId);
			redirectURI = API_V1_AUTH + AUTH_SIGNUP;
		} else {
			jwtService.setIssuedToken(response, userInfo.getUserId(), userInfo.getRole().name());
			redirectURI = HOME;
		}

		redirectStrategy.sendRedirect(request, response, redirectURI);
	}
}
