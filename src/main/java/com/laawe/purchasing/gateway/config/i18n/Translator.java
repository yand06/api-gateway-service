package com.laawe.purchasing.gateway.config.i18n;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class Translator {

    private static MessageSource messageSource;

    public Translator(MessageSource messageSource) {
        Translator.messageSource = messageSource;
    }

    public static String toLocale(String messageKey) {
        try {
            return messageSource.getMessage(messageKey, null, LocaleContextHolder.getLocale());
        } catch (Exception e) {
            System.out.println("TRANSLATE FAILED --- " + e.getMessage());
            return messageKey;
        }
    }

    public static String toLocale(String messageKey, Locale locale) {
        try {
            return messageSource.getMessage(messageKey, null, locale);
        } catch (Exception e) {
            System.out.println("GATEWAY TRANSLATE ERROR --- " + e.getMessage());
            return messageKey;
        }
    }
}