package com.travelhub.travelhub_api.data.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@JsonNaming(SnakeCaseStrategy.class)
@NoArgsConstructor
public class TokenResponse {
	private String accessToken;
	private Long expiresIn;
	private String refreshToken;
	private String scope;
	private String tokenType;
}
