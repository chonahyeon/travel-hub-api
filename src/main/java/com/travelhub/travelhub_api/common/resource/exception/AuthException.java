package com.travelhub.travelhub_api.common.resource.exception;

import com.travelhub.travelhub_api.data.enums.common.ErrorCodes;
import lombok.Getter;

@Getter
public class AuthException extends RuntimeException {

    public final ErrorCodes errorCodes;

    public AuthException(ErrorCodes errorCodes) {
        super(errorCodes.getMessage());
        this.errorCodes = errorCodes;
    }
}
