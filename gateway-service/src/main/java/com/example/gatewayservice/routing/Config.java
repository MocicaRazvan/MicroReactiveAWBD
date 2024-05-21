package com.example.gatewayservice.routing;


import com.example.gatewayservice.filters.AuthFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
public class Config {

    @Bean
    public CorsWebFilter corsFilter(
            @Value("${front.url}") String frontUrl
    ) {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin(frontUrl);
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.addAllowedHeader("*");
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type", "Accept", "X-Requested-With", "Origin"));
        configuration.setExposedHeaders(Arrays.asList("Access-Control-Allow-Origin", "Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return new CorsWebFilter(source);
    }

    @Bean
    AntPathMatcher antPathMatcher() {
        return new AntPathMatcher();
    }

    @Bean
    public RouteLocator gatewayRouteLocator(RouteLocatorBuilder builder, AuthFilter authFilter) {
        return builder.routes()
                .route("internal-paths", r -> r.path("/posts/internal/**", "/comments/internal/**", "/users/internal/**", "/exercises/internal/**", "/trainings/internal/**",
                                "/orders/internal/**")
                        .filters(f -> f.filter(((exchange, chain) -> {
                            exchange.getResponse().setStatusCode(HttpStatus.NOT_FOUND);
                            return exchange.getResponse().setComplete();

                        }))).uri("no://op"))
                .route("auth-service", r -> r.path("/auth/**")
                        .filters(f -> f.filter(authFilter))
                        .uri("lb://user-service"))
                .route("user-service", r -> r.path("/users/**")
                        .filters(f -> f.filter(authFilter))
                        .uri("lb://user-service"))
                .route("post-service", r -> r.path("/posts/**")
                        .filters(f -> f.filter(authFilter))
                        .uri("lb://post-service"))
                .route("comment-service", r -> r.path("/comments/**")
                        .filters(f -> f.filter(authFilter))
                        .uri("lb://comment-service"))
                .route("exercise-service", r -> r.path("/exercises/**")
                        .filters(f -> f.filter(authFilter))
                        .uri("lb://exercise-service"))
                .route("training-service", r -> r.path("/trainings/**")
                        .filters(f -> f.filter(authFilter))
                        .uri("lb://training-service"))
                .route("order-service", r -> r.path("/orders/**")
                        .filters(f -> f.filter(authFilter))
                        .uri("lb://order-service"))
                .route("invoice-service", r -> r.path("/invoices/**")
                        .filters(f -> f.filter(authFilter))
                        .uri("lb://invoice-service"))


                .route("user-openapi", r -> r.path("/user-service/v3/api-docs")
                        .uri("lb://user-service"))
                .route("post-openapi", r -> r.path("/post-service/v3/api-docs")
                        .uri("lb://post-service"))
                .route("comment-openapi", r -> r.path("/comment-service/v3/api-docs")
                        .uri("lb://comment-service"))
                .route("exercise-openapi", r -> r.path("/exercise-service/v3/api-docs")
                        .uri("lb://exercise-service"))
                .route("training-openapi", r -> r.path("/training-service/v3/api-docs")
                        .uri("lb://training-service"))
                .route("order-openapi", r -> r.path("/order-service/v3/api-docs")
                        .uri("lb://order-service"))
                .route("invoice-openapi", r -> r.path("/invoice-service/v3/api-docs")
                        .uri("lb://invoice-service"))

                .build();
    }
}
