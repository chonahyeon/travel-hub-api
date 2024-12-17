package com.travelhub.travelhub_api.data.enums.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;


@Getter
@AllArgsConstructor
public enum ErrorCodes {
    // server
    SERVER_ERROR(INTERNAL_SERVER_ERROR, "SYS-001","서버 오류 발생. 관리자에게 문의바랍니다."),

    // auth
    TOKEN_EXPIRE(UNAUTHORIZED, "AUTH-001", "토큰이 만료되었습니다."),
    TOKEN_INVALID(UNAUTHORIZED, "AUTH-002", "유효하지 않은 사용자입니다."),

    /*
     * request
     *   - contents, place, review
     */
    INVALID_PARAM(BAD_REQUEST, "REQ-001", "%s 의 값이 유효하지 않습니다."),
    PLACE_NOT_FOUND(NOT_FOUND, "REQ-002", "유효하지 않은 장소입니다.");


    private final HttpStatus status;
    private final String code;
    private final String message;
}
