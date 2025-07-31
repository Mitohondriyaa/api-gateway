package io.github.mitohondriyaa.gateway.route;

import io.github.mitohondriyaa.gateway.filter.ServicesFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import reactor.core.scheduler.Schedulers;

@Configuration
@RequiredArgsConstructor
public class Routes {
    private final ServicesFilter servicesFilter;
    @Value("${product.url}")
    private String productUrl;
    @Value("${inventory.url}")
    private String inventoryUrl;
    @Value("${order.url}")
    private String orderUrl;

    @Bean
    public RouteLocator productServiceRoute(RouteLocatorBuilder builder) {
        return builder.routes()
            .route("product_service", r -> r.path("/api/product/**")
                .and().method(HttpMethod.GET, HttpMethod.POST)
                .filters(f -> f.filter(servicesFilter)
                    .filter(((exchange, chain) -> chain.filter(exchange)
                        .subscribeOn(Schedulers.boundedElastic()))))
                .uri(productUrl))
            .build();
    }

    @Bean
    public RouteLocator orderServiceRoute(RouteLocatorBuilder builder) {
        return builder.routes()
            .route("order_service", r -> r.path("/api/order/**")
                .and().method(HttpMethod.POST)
                .filters(f -> f.filter(servicesFilter)
                    .filter(((exchange, chain) -> chain.filter(exchange)
                        .subscribeOn(Schedulers.boundedElastic()))))
                .uri(orderUrl))
            .build();
    }

    @Bean
    public RouteLocator inventoryServiceRoute(RouteLocatorBuilder builder) {
        return builder.routes()
            .route("inventory_service", r -> r.path("/api/inventory/**")
                .and().method(HttpMethod.GET)
                .filters(f -> f.filter(servicesFilter)
                    .filter(((exchange, chain) -> chain.filter(exchange)
                        .subscribeOn(Schedulers.boundedElastic()))))
                .uri(inventoryUrl))
            .build();
    }

    @Bean
    public RouteLocator productServiceSwaggerRoute(RouteLocatorBuilder builder) {
        return builder.routes()
            .route("product_service_swagger", r -> r.path("/aggregate/product-service/swagger-api")
                .filters(f -> f.rewritePath("/aggregate/product-service/swagger-api", "/swagger-api"))
                .uri(productUrl))
            .build();
    }

    @Bean
    public RouteLocator orderServiceSwaggerRoute(RouteLocatorBuilder builder) {
        return builder.routes()
            .route("order_service_swagger", r -> r.path("/aggregate/order-service/swagger-api")
                .filters(f -> f.rewritePath("/aggregate/order-service/swagger-api", "/swagger-api"))
                .uri(orderUrl))
            .build();
    }

    @Bean
    public RouteLocator inventoryServiceSwaggerRoute(RouteLocatorBuilder builder) {
        return builder.routes()
            .route("inventory_service_swagger", r -> r.path("/aggregate/inventory-service/swagger-api")
                .filters(f -> f.rewritePath("/aggregate/inventory-service/swagger-api", "/swagger-api"))
                .uri(inventoryUrl))
            .build();
    }
}