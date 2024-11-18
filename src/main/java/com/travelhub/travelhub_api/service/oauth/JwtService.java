package com.travelhub.travelhub_api.service.oauth;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.travelhub.travelhub_api.data.dto.UserSessionDto;
import com.travelhub.travelhub_api.data.dto.UserTokenDto;
import com.travelhub.travelhub_api.data.enums.Role;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class JwtService {

	private final SecretKey key;
	private final long accessTokenExpireTime;

	public JwtService(@Value("${jwt.secret}") String key, @Value("${jwt.expiration_time}") long accessTokenExpireTime) {
		byte[] decode = Base64.getDecoder().decode(key);
		this.key = Keys.hmacShaKeyFor(decode);
		this.accessTokenExpireTime = accessTokenExpireTime;
	}

	/**
	 * 토큰 발급
	 * @param userInfo user 정보
	 * @return 토큰
	 */
	public String createToken(UserSessionDto userInfo) {
		// user 정보
		Map<String, Object> attribute = userInfo.getAttribute();
		String usId = attribute.get(userInfo.getName()).toString();

		Claims claims = Jwts.claims();
		claims.put("usId", usId);
		claims.put("usRole", userInfo.getRole().name());

		LocalDateTime now = LocalDateTime.now();
		LocalDateTime expiration = now.plusSeconds(accessTokenExpireTime);

		return Jwts.builder()
				.setClaims(claims)
				.setIssuedAt(Timestamp.valueOf(now))
				.setExpiration(Timestamp.valueOf(expiration))
				.signWith(key, SignatureAlgorithm.HS256)
				.compact();
	}

	/**
	 * 토큰 복호화
	 * @param token 토쿤
	 * @return 유효한 토큰 여부
	 */
	public UserTokenDto parseToken(String token) {
		try {
			Claims claims = Jwts.parserBuilder()
				.setSigningKey(key)
				.build()
				.parseClaimsJws(token)
				.getBody();

			return UserTokenDto.builder()
					.usId(claims.get("usId").toString())
					.role(Role.valueOf(claims.get("usRole").toString()))
					.build();
		} catch (ExpiredJwtException expiredJwtException) {
			log.warn("parseToken() : expire token", expiredJwtException);
		} catch (Exception e) {
			log.error("parseToken() : invalid token", e);
		}
		throw new IllegalArgumentException();
	}
}
