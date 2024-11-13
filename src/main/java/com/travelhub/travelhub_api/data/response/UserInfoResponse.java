package com.travelhub.travelhub_api.data.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString
public class UserInfoResponse {
    private String id;
    private String email;
    private Boolean verifiedEmail;
    private String name;
    private String picture;
}
