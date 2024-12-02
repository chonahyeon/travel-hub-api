package com.travelhub.travelhub_api.data.common;

import com.travelhub.travelhub_api.data.enums.common.ErrorCodes;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ErrorResponse {
    private String code;
    private String message;

    public static ErrorResponse of(ErrorCodes errorCode) {
        return ErrorResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();
    }
}
