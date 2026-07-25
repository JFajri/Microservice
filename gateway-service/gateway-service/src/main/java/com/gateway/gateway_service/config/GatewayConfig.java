package com.gateway.gateway_service.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
public class GatewayConfig {

    private final AuthenticationFilterFactory filterFactory;

    private static final Logger log =
        LoggerFactory.getLogger(GatewayConfig.class);

    public GatewayConfig(AuthenticationFilterFactory filterFactory) {
        this.filterFactory = filterFactory;
    }

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {

        return builder.routes()

            .route("auth-service", r -> {
                log.info("Meneruskan ke AUTH-SERVICE");
                return r.path("/api/v1/auth/**")
                        .uri("lb://AUTH-SERVICE");
            })

            // PRODUK
            .route("produk-service", r -> {
                log.info("Meneruskan ke PRODUK");
                return r.path("/api/produk/**")
                        .uri("lb://PRODUK");
            })

            // PELANGGAN
            .route("pelanggan-service", r -> {
                log.info("Meneruskan ke PELANGGAN");
                return r.path("/api/pelanggan/**")
                        .uri("lb://PELANGGAN");
            })

            // ORDER
            .route("order-service", r -> {
                log.info("Meneruskan ke ORDER");
                return r.path("/api/order/**")
                        .filters(f -> f.filter(
                                filterFactory.apply(new AuthenticationFilterFactory.Config())))
                        .uri("lb://ORDER");
            })

            // PRODUSER
            .route("produser-service", r -> {
                log.info("Meneruskan ke PRODUSER");
                return r.path("/api/produser/**")
                        .uri("lb://PRODUSER");
            })

            // CONSUMER
            .route("consumer-service", r -> {
                log.info("Meneruskan ke CONSUMER");
                return r.path("/api/consumer/**")
                        .uri("lb://CONSUMER");
            })

            .build();
    }
}
