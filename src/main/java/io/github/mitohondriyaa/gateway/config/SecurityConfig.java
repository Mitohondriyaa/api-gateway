package io.github.mitohondriyaa.gateway.config;

import io.github.mitohondriyaa.gateway.converter.KeycloakRealmRoleConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import reactor.core.publisher.Mono;

@Configuration
public class SecurityConfig {
    @Bean
    public ServerAuthenticationEntryPoint authenticationEntryPoint() {
        return (exchange, ex) -> {
            byte[] body = """
                {
                    "code": "UNAUTHORIZED",
                    "message": "User not authenticated"
                }
                """.getBytes();
            DataBuffer dataBuffer = exchange.getResponse().bufferFactory().wrap(body);

            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);

            return exchange.getResponse().writeWith(Mono.just(dataBuffer));
        };
    }

    @Bean
    public ServerAccessDeniedHandler accessDeniedHandler() {
        return (exchange, denied) -> {
            byte[] body = """
                {
                    "code": "FORBIDDEN",
                    "message": "User not authorized"
                }
                """.getBytes();
            DataBuffer dataBuffer = exchange.getResponse().bufferFactory().wrap(body);

            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
            return exchange.getResponse().writeWith(Mono.just(dataBuffer));
        };
    }

    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
        return http.authorizeExchange(exchange -> exchange
                .pathMatchers(
                    "/swagger-ui/**",
                    "/swagger-api/**",
                    "/swagger-ui.html",
                    "/aggregate/**"
                )
                .permitAll()
                .pathMatchers("/api/inventory/**")
                .hasRole("INVENTORY_MANAGER")
                .anyExchange()
                .authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2
                    .authenticationEntryPoint(authenticationEntryPoint())
                    .accessDeniedHandler(accessDeniedHandler())
                    .jwt(jwt -> jwt.jwtAuthenticationConverter(new KeycloakRealmRoleConverter())))
                .build();
    }
}