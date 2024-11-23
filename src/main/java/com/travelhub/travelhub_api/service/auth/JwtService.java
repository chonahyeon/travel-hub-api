package com.travelhub.travelhub_api.service.auth;

import com.travelhub.travelhub_api.common.resource.exception.AuthException;
import com.travelhub.travelhub_api.data.dto.auth.LoginUserEventDto;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;

@Slf4j
@Service
public class JwtService {

	private final RedisService<String, LoginUserEventDto> redisService;
	private final SecretKey key;
	private final long accessTokenExpireTime;

	public JwtService(@Value("${jwt.secret}") String key, RedisService<String, LoginUserEventDto> redisService, @Value("${jwt.expiration_time}") long accessTokenExpireTime) {
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
		Optional<LoginUserEventDto> loginUserEventDto = redisService.get(usId);
		LoginUserEventDto userEventDto = loginUserEventDto.orElseThrow(AuthException::new);

		String compareToken = isRefresh ? userEventDto.getRefreshToken() : userEventDto.getAccessToken();
		if (!compareToken.equals(token)) throw new AuthException();
	}

	/*
	 * 인증된 사용자에 대해 토큰 정보 쿠키 할당 및 Redis 저장
	 */
	public void setIssuedToken(HttpServletResponse response, String usId, String role, String token) {
		String accessToken = createToken(usId, role, false);
		String refreshToken = token != null ? token : createToken(usId, role, true);

		// set cookie token info
		ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", accessToken)
				.httpOnly(true)
				.secure(false)
				.path("/")
				.maxAge(Duration.of(1, ChronoUnit.HOURS))
				.build();

		ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", refreshToken)
				.httpOnly(true)
				.secure(false)
				.path("/")
				.maxAge(Duration.of(3, ChronoUnit.HOURS))
				.build();

		response.setHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
		response.setHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

		// redis 에 사용자 정보 저장
		LoginUserEventDto loginUserEventDto = LoginUserEventDto.builder()
				.userId(usId)
				.accessToken(accessToken)
				.refreshToken(refreshToken)
				.build();

		redisService.save(usId, loginUserEventDto);
	}

	/**
	 * 로그아웃 시, 토큰 무효화 처리
	 */
	public void invalidateToken(String usId) {
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
		Optional<String> refreshToken = findCookie(cookies, "refreshToken");
		String token = refreshToken.orElseThrow(AuthException::new);

		Claims claims = parseToken(token, true);
		String usId = claims.get("usId").toString();
		String usRole = claims.get("usRole").toString();

		validUser(usId, token, true);
		setIssuedToken(response, usId, usRole, token);
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
			if (isRefresh) throw new AuthException();
			else throw e;
		}
	}

	/**
	 * cookie 에서 필요한 값 파싱
	 * @param cookies 대상 쿠키
	 * @param key 파싱할 key
	 * @return value
	 */
	public Optional<String> findCookie(Cookie[] cookies, String key) {
		return Arrays.stream(cookies)
				.filter(cookie -> cookie.getName().equals(key))
				.map(Object::toString)
				.findAny();
	}
}
