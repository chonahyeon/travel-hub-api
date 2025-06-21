package com.travelhub.travelhub_api.controller.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import static com.travelhub.travelhub_api.data.enums.common.ResponseCodes.SUCCESS_OK;

@Getter
@AllArgsConstructor(staticName = "of")
public class ApiResponse<T> {
    private final String code;
    private final String message;
    private final T data;

    public static <T> ApiResponse<T> of(String code, String message) {
        return ApiResponse.of(code, message, null);
    }

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.of(SUCCESS_OK.getCode(), SUCCESS_OK.getMessage(), data);
    }

    public static <T> ApiResponse<T> success() {
        return ApiResponse.of(SUCCESS_OK.getCode(), SUCCESS_OK.getMessage(), null);
    }

}
