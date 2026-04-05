package com.laawe.purchasing.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import javax.crypto.spec.SecretKeySpec;

import java.util.Arrays;
import java.util.List;

import static com.laawe.purchasing.gateway.config.constant.AppConstant.*;

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
                .cors(Customizer.withDefaults())
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
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of(CORS_ALLOWED_ORIGINS));
        configuration.setAllowedMethods(
                Arrays.asList(
                        GET_HTTP_METHOD,
                        POST_HTTP_METHOD,
                        PUT_HTTP_METHOD,
                        DELETE_HTTP_METHOD,
                        OPTIONS_HTTP_METHOD,
                        PATCH_HTTP_METHOD
                )
        );
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    @Bean
    public ReactiveJwtDecoder jwtDecoder() {
        SecretKeySpec secretKeySpec = new SecretKeySpec(this.jwtSecretKey.getBytes(), ALGORITHM);
        return NimbusReactiveJwtDecoder.withSecretKey(secretKeySpec).build();
    }

}
