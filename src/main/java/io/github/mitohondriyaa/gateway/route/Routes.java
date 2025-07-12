package io.github.mitohondriyaa.gateway.route;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Routes {
    @Bean
    public RouteLocator productServiceRoute(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("product_service", r -> r.path("/api/product")
                        .uri("http://localhost:8080"))
                .build();
    }

    @Bean
    public RouteLocator orderServiceRoute(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("order_service", r -> r.path("/api/order")
                        .uri("http://localhost:8081"))
                .build();
    }

    @Bean
    public RouteLocator inventoryServiceRoute(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("inventory_service", r -> r.path("/api/inventory")
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
}