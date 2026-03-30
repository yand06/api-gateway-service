package com.laawe.purchasing.gateway.config.constant;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ResponseCode {

    // --- SUCCESS CODES ---
    SUCCESS(HttpStatus.OK, "00", "response.code.success"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "07","response.code.unauthorized"),

    // --- SERVER ERRORS ---
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "99", "response.code.internal_server_error");

    private final HttpStatus httpStatus;
    private final String code;

    private final String messageKey;

    ResponseCode(HttpStatus httpStatus, String code, String messageKey) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.messageKey = messageKey;
    }
}