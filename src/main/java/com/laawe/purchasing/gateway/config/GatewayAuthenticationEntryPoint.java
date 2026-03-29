package com.laawe.purchasing.gateway.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

            var responseBody = new ErrorResponse(
                    "ERROR",
                    "07",
                    "Akses ditolak! Token tidak valid, telah kedaluwarsa, atau tidak ditemukan."
            );

            try {
                // Konversi objek Java menjadi byte array JSON
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