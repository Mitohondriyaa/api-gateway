package io.github.mitohondriyaa.gateway.handler;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Map;

@Component
public class GatewayHandler {
    private static final Map<String, String> fallbackBody
        = Collections.singletonMap("info", "Service unavailable, please try again later");

    public Mono<ServerResponse> fallbackHandle(ServerRequest request) {
        return ServerResponse.status(HttpStatus.SERVICE_UNAVAILABLE)
            .body(Mono.just(fallbackBody), Map.class);
    }
}