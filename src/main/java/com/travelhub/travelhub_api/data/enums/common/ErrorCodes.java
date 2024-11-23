package com.travelhub.travelhub_api.data.enums.common;

import lombok.Getter;

@Getter
public enum ErrorCodes {
    TOKEN_EXPIRE("ERR001", "토큰이 만료되었습니다"),
    TOKEN_INVALID("ERR002", "유효하지 않은 사용자입니다");

    private final String code;
    private final String message;

    ErrorCodes(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
