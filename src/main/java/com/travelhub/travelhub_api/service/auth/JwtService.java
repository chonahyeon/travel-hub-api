package com.travelhub.travelhub_api.service.auth;

import com.travelhub.travelhub_api.common.resource.exception.AuthException;
import com.travelhub.travelhub_api.common.util.CookieUtil;
import com.travelhub.travelhub_api.data.dto.auth.LoginUserEventDTO;
import com.travelhub.travelhub_api.service.common.RedisService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Optional;

import static com.travelhub.travelhub_api.common.resource.TravelHubResource.AUTH_ACCESS_TOKEN;
import static com.travelhub.travelhub_api.common.resource.TravelHubResource.AUTH_REFRESH_TOKEN;
import static com.travelhub.travelhub_api.data.enums.common.ErrorCodes.INVALID_USER;

@Slf4j
@Service
public class JwtService {

	private final RedisService<String, LoginUserEventDTO> redisService;
	private final SecretKey key;
	private final long accessTokenExpireTime;

	public JwtService(@Value("${jwt.secret}") String key, RedisService<String, LoginUserEventDTO> redisService, @Value("${jwt.expiration_time}") long accessTokenExpireTime) {
        this.redisService = redisService;
        byte[] decode = Base64.getDecoder().decode(key);
		this.key = Keys.hmacShaKeyFor(decode);
		this.accessTokenExpireTime = accessTokenExpireTime;
	}

	/**
	 * 기존에 인증된 사용자인지 유효성 검사 진행
	 * @param usId user 고유값
	 * @param token accessToken
	 */
	public void validUser(String usId, String token, boolean isRefresh) {
		Optional<LoginUserEventDTO> loginUserEventDto = redisService.get(usId);
		LoginUserEventDTO userEventDto = loginUserEventDto.orElseThrow(() -> new AuthException(INVALID_USER));

		String compareToken = isRefresh ? userEventDto.getRefreshToken() : userEventDto.getAccessToken();
		if (!compareToken.equals(token)) {
			throw new AuthException(INVALID_USER);
		}
	}

	/*
	 * 인증된 사용자에 대해 토큰 정보 쿠키 할당 및 Redis 저장
	 * 갱신 시, refresh 도 함께 갱신하는 Token Rotation Policy 적용
	 */
	public void setIssuedToken(HttpServletResponse response, String usId, String role) {
		String accessToken = createToken(usId, role, false);
		String refreshToken = createToken(usId, role, true);

		// set cookie token info
		CookieUtil.setCookie(response, Duration.of(1, ChronoUnit.HOURS), AUTH_ACCESS_TOKEN, accessToken);
		CookieUtil.setCookie(response, Duration.of(3, ChronoUnit.HOURS), AUTH_REFRESH_TOKEN, refreshToken);

		// redis 에 사용자 정보 저장
		LoginUserEventDTO loginUserEventDto = LoginUserEventDTO.builder()
				.userId(usId)
				.accessToken(accessToken)
				.refreshToken(refreshToken)
				.build();

		redisService.save(usId, loginUserEventDto);
	}

	/**
	 * 로그아웃 시, 토큰 무효화 처리
	 * Redis 삭제 처리 및 쿠키 만료 처리
	 */
	public void invalidateToken(HttpServletResponse response, String usId) {
		// 쿠키 만료 처리
		CookieUtil.setCookie(response, Duration.of(0, ChronoUnit.SECONDS), AUTH_ACCESS_TOKEN, "");
		CookieUtil.setCookie(response, Duration.of(0, ChronoUnit.SECONDS), AUTH_REFRESH_TOKEN, "");

		// redis 삭제
        redisService.delete(usId);
	}

	/**
	 * 토큰 발급
	 * @param usId user id
	 * @param role user role
	 * @return 토큰
	 */
	private String createToken(String usId, String role, boolean isRefresh) {
		// user 정보
		Claims claims = Jwts.claims();
		claims.put("usId", usId);
		claims.put("usRole", role);

		LocalDateTime now = LocalDateTime.now();
		long tokenExpireTime = (isRefresh ? accessTokenExpireTime * 3 : accessTokenExpireTime);
		LocalDateTime expiration = now.plusSeconds(tokenExpireTime);

		return Jwts.builder()
				.setClaims(claims)
				.setIssuedAt(Timestamp.valueOf(now))
				.setExpiration(Timestamp.valueOf(expiration))
				.signWith(key, SignatureAlgorithm.HS256)
				.compact();
	}

	/**
	 * 만료된 accessToken 갱신.
	 * @param response servlet response
	 * @param cookies 사용자 쿠키 정보
	 */
	public void renewToken(HttpServletResponse response, Cookie[] cookies) {
		Optional<String> refreshToken = CookieUtil.findCookie(cookies, AUTH_REFRESH_TOKEN);
		String token = refreshToken.orElseThrow(() -> new AuthException(INVALID_USER));

		Claims claims = parseToken(token, true);
		String usId = claims.get("usId").toString();
		String usRole = claims.get("usRole").toString();

		validUser(usId, token, true);
		setIssuedToken(response, usId, usRole);
	}

	/**
	 * 토큰 복호화
	 * refresh 가 만료된 경우에는 재로그인
	 * @param token 토큰
	 * @return 유효한 토큰 여부
	 */
	public Claims parseToken(String token, boolean isRefresh) {
		try {
			return Jwts.parserBuilder()
					.setSigningKey(key)
					.build()
					.parseClaimsJws(token)
					.getBody();
		} catch (ExpiredJwtException e) {
			if (isRefresh) throw new AuthException(INVALID_USER);
			else throw e;
		}
	}
}
