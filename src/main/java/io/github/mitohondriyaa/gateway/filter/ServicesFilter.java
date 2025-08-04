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
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

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
            .onErrorResume(exception -> {
                ServerHttpResponse response = exchange.getResponse();
                response.setStatusCode(HttpStatus.SERVICE_UNAVAILABLE);
                byte[] responseBody = """
                    {
                        "message": "Service unavailable, please try again later"
                    }
                    """.getBytes();
                DataBuffer dataBuffer = response.bufferFactory().wrap(responseBody);
                response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
                return response.writeWith(Mono.just(dataBuffer));
            });
    }
}