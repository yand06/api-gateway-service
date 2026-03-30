package com.laawe.purchasing.gateway.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.laawe.purchasing.gateway.config.constant.ResponseCode;
import com.laawe.purchasing.gateway.config.i18n.Translator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Locale;

import static com.laawe.purchasing.gateway.config.constant.AppConstant.ERROR_STATUS;

@Slf4j
@Component
@RequiredArgsConstructor
public class GatewayAuthenticationEntryPoint implements ServerAuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException ex) {
        log.warn("Akses ditolak di API Gateway (Unauthorized): {}", ex.getMessage());

        return Mono.defer(() -> {
            var response = exchange.getResponse();
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

            Locale userLocale = exchange.getRequest().getHeaders().getAcceptLanguageAsLocales()
                    .stream()
                    .findFirst()
                    .orElse(Locale.getDefault());
            var responseBody = new ErrorResponse(
                    ERROR_STATUS,
                    ResponseCode.UNAUTHORIZED.getCode(),
                    Translator.toLocale(ResponseCode.UNAUTHORIZED.getMessageKey(), userLocale)
            );

            try {
                byte[] bytes = objectMapper.writeValueAsBytes(responseBody);
                DataBuffer buffer = response.bufferFactory().wrap(bytes);

                return response.writeWith(Mono.just(buffer));
            } catch (JsonProcessingException e) {
                log.error("Gagal melakukan serialisasi JSON pada error response", e);
                return Mono.error(e);
            }
        });
    }

    private record ErrorResponse(String status, String code, String message) {}
}