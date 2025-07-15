package io.github.mitohondriyaa.gateway.filter;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;

@Component
@RequiredArgsConstructor
public class ServicesFilter implements GatewayFilter {
    private final CircuitBreakerRegistry circuitBreakerRegistry;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String name = exchange.getRequest().getURI().getPath()
            .substring(5);
        CircuitBreaker circuitBreaker
            = circuitBreakerRegistry.circuitBreaker(name + "ServiceCircuitBreaker");
        CircuitBreaker.State circuitBreakerState = circuitBreaker.getState();

        if (circuitBreakerState == CircuitBreaker.State.OPEN) {
            exchange.getResponse().setStatusCode(HttpStatus.SERVICE_UNAVAILABLE);
            exchange.getResponse().getHeaders().setLocation(URI.create("/fallback"));

            return exchange.getResponse().setComplete();
        }

        return chain.filter(exchange);
    }
}