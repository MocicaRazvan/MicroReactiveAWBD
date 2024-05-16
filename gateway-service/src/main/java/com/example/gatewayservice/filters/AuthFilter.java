package com.example.gatewayservice.filters;


import com.example.gatewayservice.clients.UserClient;
import com.example.gatewayservice.dtos.TokenValidationRequest;
import com.example.gatewayservice.dtos.TokenValidationResponse;
import com.example.gatewayservice.enums.Role;
import com.example.gatewayservice.routing.RouteValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Component
@Slf4j
public class AuthFilter implements GatewayFilter {


    private final UserClient userClient;
    private final RouteValidator routeValidator;
    private final ObjectMapper objectMapper;

    public AuthFilter(UserClient userClient, RouteValidator routeValidator, ObjectMapper objectMapper) {
        this.userClient = userClient;
        this.routeValidator = routeValidator;
        this.objectMapper = objectMapper;
    }


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        if (exchange.getRequest().getMethod() == HttpMethod.OPTIONS) {
            log.error("Request: {}", exchange.getRequest().getMethod());
            return chain.filter(exchange);
        }

        Role role = routeValidator.getMinRole(exchange.getRequest());
        log.info("Role: {}", role);
        if (role == null) {
            return chain.filter(exchange);
        }

        final String authCookie = exchange.getRequest().getCookies().getFirst("authToken") != null ?
                Objects.requireNonNull(exchange.getRequest().getCookies().getFirst("authToken")).getValue() : null;

        log.info("AuthCookie: {}", authCookie);

        final String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        log.error(exchange.getRequest().getHeaders().toString());

        log.info("AuthHeader: {}", authHeader);

        if ((authHeader == null || !authHeader.startsWith("Bearer ")) && (authCookie == null || authCookie.isEmpty())) {
            return handleError("Token not found", exchange);
        }

        final String token = authHeader != null ? authHeader.substring(7) : authCookie;

        TokenValidationRequest request = TokenValidationRequest.builder()
                .token(token).minRoleRequired(role).build();

        return userClient.validateToken(request)
                .flatMap(resp -> {
                    if (!resp.isValid()) {
                        return handleError("Token is not valid", exchange);
                    }
                    exchange.getRequest().mutate()
                            .header("x-auth-user-id", resp.getUserId().toString());
                    return chain.filter(exchange);

                });


    }

    private Mono<Void> handleError(String message, ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        Map<String, Object> resp = new HashMap<>();
        resp.put("message", message);
        resp.put("timestamp", Instant.now().toString());
        resp.put("error", HttpStatus.UNAUTHORIZED.getReasonPhrase());
        resp.put("status", HttpStatus.UNAUTHORIZED.value());
        resp.put("path", exchange.getRequest().getPath().value());
        try {
            return response.writeWith(Mono.just(response.bufferFactory().wrap(objectMapper.writeValueAsBytes(resp))));
        } catch (Exception e) {
            log.error("Error while writing response", e);
            return Mono.error(e);
        }
    }
}
