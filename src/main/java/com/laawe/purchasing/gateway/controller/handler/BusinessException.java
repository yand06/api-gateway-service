package com.laawe.purchasing.gateway.controller.handler;

import com.laawe.purchasing.gateway.config.constant.ResponseCode;
import com.laawe.purchasing.gateway.config.i18n.Translator;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private final ResponseCode responseCode;

    public BusinessException(ResponseCode responseCode) {
        super(Translator.toLocale(responseCode.getMessageKey()));
        this.responseCode = responseCode;
    }

    public BusinessException(ResponseCode responseCode, String customMessage) {
        super(customMessage);
        this.responseCode = responseCode;
    }
}