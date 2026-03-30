package com.laawe.purchasing.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

import javax.crypto.spec.SecretKeySpec;

import static com.laawe.purchasing.gateway.config.AppConstant.*;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Value("${secret.key}")
    private String jwtSecretKey;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(
            ServerHttpSecurity http,
            GatewayAuthenticationEntryPoint entryPoint
    ) {

        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchange -> exchange
                        .pathMatchers(
                                BASE_API_URL + LOGIN_API,
                                BASE_API_URL + REGISTER_API,
                                BASE_API_URL + REFRESH_TOKEN_API
                        ).permitAll()
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(jwt -> jwt.jwtDecoder(jwtDecoder()))
                        .authenticationEntryPoint(entryPoint)
                )
                .exceptionHandling(e -> e
                        .authenticationEntryPoint(entryPoint)
                )
                .build();
    }

    @Bean
    public ReactiveJwtDecoder jwtDecoder() {
        SecretKeySpec secretKeySpec = new SecretKeySpec(this.jwtSecretKey.getBytes(), ALGORITHM);
        return NimbusReactiveJwtDecoder.withSecretKey(secretKeySpec).build();
    }

}
