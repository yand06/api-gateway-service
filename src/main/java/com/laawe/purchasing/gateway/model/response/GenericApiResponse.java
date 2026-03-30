package com.laawe.purchasing.gateway.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.laawe.purchasing.gateway.config.constant.ResponseCode;
import com.laawe.purchasing.gateway.config.i18n.Translator;
import lombok.Data;
import lombok.experimental.Accessors;

import static com.laawe.purchasing.gateway.config.constant.AppConstant.ERROR_STATUS;
import static com.laawe.purchasing.gateway.config.constant.AppConstant.SUCCESS_STATUS;

@Data
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GenericApiResponse<T> {
    private String status;
    private String code;
    private String message;
    private T data;

    public static <T> GenericApiResponse<T> success(T data) {
        return new GenericApiResponse<T>()
                .setStatus(SUCCESS_STATUS)
                .setCode(ResponseCode.SUCCESS.getCode())
                .setMessage(Translator.toLocale(ResponseCode.SUCCESS.getMessageKey()))
                .setData(data);
    }

    public static <T> GenericApiResponse<T> success(T data, String customMessage) {
        return new GenericApiResponse<T>()
                .setStatus(SUCCESS_STATUS)
                .setCode(ResponseCode.SUCCESS.getCode())
                .setMessage(customMessage)
                .setData(data);
    }

    public static <T> GenericApiResponse<T> error(ResponseCode responseCode) {
        return new GenericApiResponse<T>()
                .setStatus(ERROR_STATUS)
                .setCode(responseCode.getCode())
                .setMessage(Translator.toLocale(responseCode.getMessageKey()))
                .setData(null);
    }

    public static <T> GenericApiResponse<T> error(ResponseCode responseCode, T data) {
        return new GenericApiResponse<T>()
                .setStatus(ERROR_STATUS)
                .setCode(responseCode.getCode())
                .setMessage(Translator.toLocale(responseCode.getMessageKey()))
                .setData(data);
    }
}