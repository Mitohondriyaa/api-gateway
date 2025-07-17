package io.github.mitohondriyaa.gateway.filter;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import io.github.resilience4j.reactor.timelimiter.TimeLimiterOperator;
import io.github.resilience4j.timelimiter.TimeLimiter;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;
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
    private final TimeLimiterRegistry timeLimiterRegistry;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String name = exchange.getRequest().getURI().getPath()
            .substring(5);
        CircuitBreaker circuitBreaker
            = circuitBreakerRegistry.circuitBreaker(name + "ServiceCircuitBreaker");
        TimeLimiter timeLimiter
            = timeLimiterRegistry.timeLimiter(name + "ServiceTimeLimiter");

        return chain.filter(exchange)
            .transformDeferred(TimeLimiterOperator.of(timeLimiter))
            .transform(CircuitBreakerOperator.of(circuitBreaker))
            .onErrorResume((throwable) ->  {
                exchange.getResponse().setStatusCode(HttpStatus.SEE_OTHER);
                exchange.getResponse().getHeaders().setLocation(URI.create("/fallback"));

                return exchange.getResponse().setComplete();
            });
    }
}