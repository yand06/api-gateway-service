package com.laawe.purchasing.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders; // ✅ Import baru
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator; // ✅ Import baru
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.util.annotation.NonNullApi;

import java.util.stream.Collectors;

import static com.laawe.purchasing.gateway.config.AppConstant.*;

@Component
public class JWTAuthFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(JWTAuthFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        // 1. SOLUSI DEFINITIF: Buat map header baru yang terpisah dari ikatan ReadOnly Spring
        HttpHeaders cleanHeaders = new HttpHeaders();
        cleanHeaders.putAll(exchange.getRequest().getHeaders());
        cleanHeaders.remove(X_USER_ID);
        cleanHeaders.remove(X_USER_NAME);
        cleanHeaders.remove(X_USER_ROLES);

        // Gunakan Decorator untuk menimpa method getHeaders()
        ServerHttpRequest cleanRequest = new ServerHttpRequestDecorator(exchange.getRequest()) {
            @Override
            public HttpHeaders getHeaders() {
                return cleanHeaders;
            }
        };

        ServerWebExchange cleanExchange = exchange.mutate().request(cleanRequest).build();
        String path = cleanExchange.getRequest().getURI().getPath();

        // 2. Bypass untuk endpoint auth
        if (path.equals(BASE_API_URL + LOGIN_API)
                || path.equals(BASE_API_URL + REGISTER_API)
                || path.equals(BASE_API_URL + REFRESH_TOKEN_API)) {
            return chain.filter(cleanExchange);
        }

        // 3. Proses JWT jika ada
        return cleanExchange.getPrincipal()
                .cast(Authentication.class)
                .flatMap(auth -> {
                    Jwt jwt = (Jwt) auth.getPrincipal();

                    String userId = jwt.getClaimAsString(USER_ID) != null ? jwt.getClaimAsString(USER_ID) : auth.getName();
                    String username = jwt.getClaimAsString(USERNAME) != null ? jwt.getClaimAsString(USERNAME) : userId;
                    String roles = auth.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority)
                            .collect(Collectors.joining(COMMA));

                    log.debug("REQUEST FROM userId={}, path={}", userId, path);

                    // 4. Inject header yang sudah divalidasi ke request baru
                    HttpHeaders authenticatedHeaders = new HttpHeaders();
                    authenticatedHeaders.putAll(cleanHeaders);
                    authenticatedHeaders.set(X_USER_ID, userId);
                    authenticatedHeaders.set(X_USER_NAME, username);
                    authenticatedHeaders.set(X_USER_ROLES, roles);

                    ServerHttpRequest authenticatedRequest = new ServerHttpRequestDecorator(cleanExchange.getRequest()) {
                        @Override
                        public HttpHeaders getHeaders() {
                            return authenticatedHeaders;
                        }
                    };

                    return chain.filter(cleanExchange.mutate().request(authenticatedRequest).build());
                })
                .switchIfEmpty(chain.filter(cleanExchange));
    }

    @Override
    public int getOrder() {
        return -1;
    }
}