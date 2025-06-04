package com.travelhub.travelhub_api.common.resource.exception;

import com.travelhub.travelhub_api.data.enums.common.ResponseCodes;
import lombok.Getter;

@Getter
public class AuthException extends RuntimeException {

    public final ResponseCodes responseCodes;

    public AuthException(ResponseCodes responseCodes) {
        super(responseCodes.getMessage());
        this.responseCodes = responseCodes;
    }
}
