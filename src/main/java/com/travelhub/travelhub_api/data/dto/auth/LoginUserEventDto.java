package com.travelhub.travelhub_api.data.dto.auth;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class LoginUserEventDto {
    private String userId;
    private String accessToken;
    private String refreshToken;

    // todo : 여기서 모든 정보가 일치하는지 체크?
}
