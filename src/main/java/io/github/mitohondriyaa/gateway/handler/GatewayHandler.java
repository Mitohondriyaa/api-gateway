package io.github.mitohondriyaa.gateway.handler;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class GatewayHandler {
    public Mono<ServerResponse> fallbackHandle(ServerRequest request) {
        return ServerResponse.status(HttpStatus.SERVICE_UNAVAILABLE)
            .body(Mono.just("Service unavailable, please try again later"), String.class);
    }
}