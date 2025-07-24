package io.github.mitohondriyaa.gateway.config;

import io.github.mitohondriyaa.gateway.converter.KeycloakRealmRoleConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
public class SecurityConfig {
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
                    .jwt(jwt -> jwt.jwtAuthenticationConverter(new KeycloakRealmRoleConverter())))
                .build();
    }
}