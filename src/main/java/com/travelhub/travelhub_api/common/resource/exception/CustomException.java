package com.travelhub.travelhub_api.common.resource.exception;

import com.travelhub.travelhub_api.data.enums.common.ErrorCodes;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    private final ErrorCodes errorCode;
    private final String formattedMessage;

    public CustomException(ErrorCodes errorCode, Object ... args) {
        this.errorCode = errorCode;
        this.formattedMessage = String.format(errorCode.getMessage(), args);
    }

    @Override
    public String getMessage() {
        return formattedMessage;
    }
}
