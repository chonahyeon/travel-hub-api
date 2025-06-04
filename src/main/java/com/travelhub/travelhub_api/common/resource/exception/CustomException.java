package com.travelhub.travelhub_api.common.resource.exception;

import com.travelhub.travelhub_api.data.enums.common.ResponseCodes;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    private final ResponseCodes errorCode;
    private final String formattedMessage;

    public CustomException(ResponseCodes errorCode, Object ... args) {
        this.errorCode = errorCode;
        this.formattedMessage = String.format(errorCode.getMessage(), args);
    }

    @Override
    public String getMessage() {
        return formattedMessage;
    }
}
