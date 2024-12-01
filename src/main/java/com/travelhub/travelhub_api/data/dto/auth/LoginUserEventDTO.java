package com.travelhub.travelhub_api.data.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoginUserEventDTO {
    private String userId;
    private String accessToken;
    private String refreshToken;
}
