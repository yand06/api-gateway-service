package com.laawe.purchasing.gateway.filter;

import com.laawe.purchasing.gateway.config.GatewayAuthenticationEntryPoint;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.text.ParseException;

import static com.laawe.purchasing.gateway.config.constant.AppConstant.BL_PREFIX;
import static com.laawe.purchasing.gateway.config.constant.AppConstant.HEADER_BEARER;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
public class BlacklistCheckFilter implements WebFilter {

    private final ReactiveStringRedisTemplate redisTemplate;
    private final GatewayAuthenticationEntryPoint entryPoint;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader != null && authHeader.startsWith(HEADER_BEARER)) {
            String token = authHeader.substring(7);

            try {
                SignedJWT signedJWT = SignedJWT.parse(token);
                String jti = signedJWT.getJWTClaimsSet().getJWTID();

                if (jti != null && !jti.isBlank()) {
                    String redisKey = BL_PREFIX + jti;

                    return redisTemplate.hasKey(redisKey)
                            .flatMap(isBlacklisted -> {
                                if (Boolean.TRUE.equals(isBlacklisted)) {
                                    log.warn("PENCEGATAN: ADA USER MENCOBA MEMAKAI TOKEN YANG SUDAH KADALUARSA! JTI --- {}", jti);
                                    return entryPoint.commence(exchange, new BadCredentialsException("TOKEN TELAH DI-BLACKLIST"));
                                }
                                return chain.filter(exchange);
                            });
                }
            } catch (ParseException e) {
                log.error("GAGAL MEM-PARSING JWT DI GATEWAY FILTER --- {}", e.getMessage());
            }
        }
        return chain.filter(exchange);
    }
}