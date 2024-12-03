package com.travelhub.travelhub_api.common.component.common;

import com.travelhub.travelhub_api.common.resource.exception.CustomException;
import com.travelhub.travelhub_api.controller.common.response.ErrorResponse;
import com.travelhub.travelhub_api.data.enums.common.ErrorCodes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    // 상황에 맞는 에러 지정하여 응답
    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<ErrorResponse> handleCustomException(CustomException ex) {
        ErrorCodes errorCode = ex.getErrorCode();
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ErrorResponse.of(errorCode));
    }

    // @RequestParam 의 필수 값이 null 일 때
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MissingServletRequestParameterException ex) {
        System.out.println(ex.getMessage());
        ErrorCodes errorCode = ErrorCodes.MISSING_PARAM;
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ErrorResponse.of(errorCode));
    }

    // 정의되지 않은 에러는 500 응답
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        ErrorCodes errorCode = ErrorCodes.SERVER_ERROR;
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ErrorResponse.of(errorCode));
    }
}
