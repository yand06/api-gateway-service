package com.laawe.purchasing.gateway.controller.handler;

import com.laawe.purchasing.gateway.config.constant.ResponseCode;
import com.laawe.purchasing.gateway.model.response.GenericApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<GenericApiResponse<Object>> handleAllOtherExceptions(Exception ex) {
        ex.printStackTrace();

        return ResponseEntity
                .status(ResponseCode.INTERNAL_SERVER_ERROR.getHttpStatus())
                .body(GenericApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR));
    }

}