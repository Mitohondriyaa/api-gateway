package io.github.mitohondriyaa.gateway.route;

import io.github.mitohondriyaa.gateway.filter.ServicesFilter;
import io.github.mitohondriyaa.gateway.handler.GatewayHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.scheduler.Schedulers;

@Configuration
@RequiredArgsConstructor
public class Routes {
    private final ServicesFilter servicesFilter;
    private final GatewayHandler  gatewayHandler;

    @Bean
    public RouteLocator productServiceRoute(RouteLocatorBuilder builder) {
        return builder.routes()
            .route("product_service", r -> r.path("/api/product")
                .filters(f -> f.filter(servicesFilter)
                    .filter(((exchange, chain) -> chain.filter(exchange)
                        .subscribeOn(Schedulers.boundedElastic()))))
                .uri("http://localhost:8080"))
            .build();
    }

    @Bean
    public RouteLocator orderServiceRoute(RouteLocatorBuilder builder) {
        return builder.routes()
            .route("order_service", r -> r.path("/api/order")
                .filters(f -> f.filter(servicesFilter)
                    .filter(((exchange, chain) -> chain.filter(exchange)
                        .subscribeOn(Schedulers.boundedElastic()))))
                .uri("http://localhost:8081"))
            .build();
    }

    @Bean
    public RouteLocator inventoryServiceRoute(RouteLocatorBuilder builder) {
        return builder.routes()
            .route("inventory_service", r -> r.path("/api/inventory")
                .filters(f -> f.filter(servicesFilter)
                    .filter(((exchange, chain) -> chain.filter(exchange)
                        .subscribeOn(Schedulers.boundedElastic()))))
                .uri("http://localhost:8082"))
            .build();
    }

    @Bean
    public RouteLocator productServiceSwaggerRoute(RouteLocatorBuilder builder) {
        return builder.routes()
            .route("product_service_swagger", r -> r.path("/aggregate/product-service/swagger-api")
                .filters(f -> f.rewritePath("/aggregate/product-service/swagger-api", "/swagger-api"))
                .uri("http://localhost:8080"))
            .build();
    }

    @Bean
    public RouteLocator orderServiceSwaggerRoute(RouteLocatorBuilder builder) {
        return builder.routes()
            .route("order_service_swagger", r -> r.path("/aggregate/order-service/swagger-api")
                .filters(f -> f.rewritePath("/aggregate/order-service/swagger-api", "/swagger-api"))
                .uri("http://localhost:8081"))
            .build();
    }

    @Bean
    public RouteLocator inventoryServiceSwaggerRoute(RouteLocatorBuilder builder) {
        return builder.routes()
            .route("inventory_service_swagger", r -> r.path("/aggregate/inventory-service/swagger-api")
                .filters(f -> f.rewritePath("/aggregate/inventory-service/swagger-api", "/swagger-api"))
                .uri("http://localhost:8082"))
            .build();
    }

    @Bean
    public RouterFunction<ServerResponse> fallbackRoute() {
        return RouterFunctions.route()
            .GET("/fallback", gatewayHandler::fallbackHandle)
            .build();
    }
}