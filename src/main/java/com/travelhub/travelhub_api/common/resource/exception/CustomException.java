package com.travelhub.travelhub_api.common.resource.exception;

import com.travelhub.travelhub_api.data.enums.common.ErrorCodes;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    private final ErrorCodes errorCode;

    public CustomException(ErrorCodes errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
