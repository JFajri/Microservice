package com.gateway.gateway_service.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    private final AuthenticationFilterFactory filterFactory;

    public GatewayConfig(AuthenticationFilterFactory filterFactory) {
        this.filterFactory = filterFactory;
    }

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
            // Rute Auth (Tanpa Filter)
            .route("auth-service", r -> r.path("/api/v1/auth/**")
                .uri("lb://AUTH-SERVICE"))
            
            // Rute Order (DENGAN FILTER)
            .route("order-route", r -> r.path("/api/order/**")
                .filters(f -> f.filter(filterFactory.apply(new AuthenticationFilterFactory.Config())))
                .uri("lb://ORDER"))
            
            // Rute Produk (Tanpa Filter dulu buat ngetes)
            .route("PRODUK", r -> r.path("/api/produk/**")
                .uri("lb://PRODUK"))
            .build();
    }
}
