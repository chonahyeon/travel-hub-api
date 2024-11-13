package com.travelhub.travelhub_api.data.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.travelhub.travelhub_api.common.resource.TravelHubResource;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.ToString;

@AllArgsConstructor
@Builder
@ToString
@JsonNaming(SnakeCaseStrategy.class)
public class TokenRequest {
	private String code;
	private String clientId;
	private String clientSecret;
	private String redirectUri;

	@Builder.Default
	private String grantType = "authorization_code";

	/*
	 * AccessToken 발급을 위한 Request
	 * redirect url 은 code 발급 path 와 일치해야함.
	 */
	public static TokenRequest ofAccessToken(String authorizationCode) {
		return TokenRequest.builder()
				.clientId(TravelHubResource.clientId)
				.clientSecret(TravelHubResource.clientSecret)
				.code(authorizationCode)
				.redirectUri("http://localhost:80/login/oauth2/code")
				.build();
	}
}
