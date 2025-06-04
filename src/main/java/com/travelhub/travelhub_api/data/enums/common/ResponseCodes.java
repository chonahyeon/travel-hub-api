package com.travelhub.travelhub_api.data.enums.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;


@Getter
@AllArgsConstructor
public enum ResponseCodes {


    /*
     * request
     *   - common(0), place(1), review(2), image(3), storage(4), contents(5), server(6), auth(7)
     */

    // success
    SUCCESS_OK          (OK,                    "SUC-001",   "성공."),

    // error
    INVALID_PARAM       (BAD_REQUEST,           "ERR-001",  "%s 의 값이 유효하지 않습니다."),
    PLACE_NOT_FOUND     (NOT_FOUND,             "ERR-100",  "유효하지 않은 장소입니다."),
    PLACE_CITY_NOT_FOUND(NOT_FOUND,             "ERR-101",  "%s 장소의 도시 정보가 없습니다."),
    CITY_NOT_FOUND      (NOT_FOUND,             "ERR-102",  "유효하지 않은 도시입니다."),
    CONTENTS_NOT_FOUND  (NOT_FOUND,             "ERR-500",  "유효하지 않은 게시글입니다."),
    STORAGE_NOT_FOUND   (NOT_FOUND,             "ERR-400",  "스토리지 정보가 없습니다."),
    SERVER_ERROR        (INTERNAL_SERVER_ERROR, "ERR-600",  "서버 오류 발생. 관리자에게 문의바랍니다."),
    TOKEN_EXPIRE        (UNAUTHORIZED,          "ERR-700", "토큰이 만료되었습니다."),
    TOKEN_INVALID       (UNAUTHORIZED,          "ERR-701", "유효하지 않은 사용자입니다."),
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
