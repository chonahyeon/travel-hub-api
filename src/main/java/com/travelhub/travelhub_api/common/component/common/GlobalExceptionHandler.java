package com.travelhub.travelhub_api.common.component.common;

import com.travelhub.travelhub_api.common.resource.exception.CustomException;
import com.travelhub.travelhub_api.controller.common.response.ErrorResponse;
import com.travelhub.travelhub_api.data.enums.common.ErrorCodes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    // @RequestParma, @PathVariable 값이 유효하지 않을 때
    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex,
                                                                          HttpHeaders headers,
                                                                          HttpStatusCode status,
                                                                          WebRequest request) {
        ErrorCodes errorCode = ErrorCodes.INVALID_PARAM;

        ErrorResponse response = ErrorResponse.builder()
                .code(errorCode.getCode())
                .message(String.format(errorCode.getMessage(), ex.getParameterName()))
                .build();

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(response);
    }

    // 상황에 맞는 에러 지정하여 응답
    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<ErrorResponse> handleCustomException(CustomException ex) {
        ErrorCodes errorCode = ex.getErrorCode();

        ErrorResponse response = ErrorResponse.builder()
                .code(errorCode.getCode())
                .message(ex.getMessage())
                .build();

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(response);
    }



}
