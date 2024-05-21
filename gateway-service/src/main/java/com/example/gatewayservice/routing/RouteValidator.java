package com.example.gatewayservice.routing;

import com.example.gatewayservice.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.util.List;
import java.util.function.Predicate;

@Component
@RequiredArgsConstructor
public class RouteValidator {

    private final AntPathMatcher antPathMatcher;

    private static final List<String> AUTH_WHITELIST = List.of(
            "/auth/login",
            "/auth/register",
            "/v3/api-docs/**",
            "/v3/api-docs.yaml",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/**",
            "/user-service/v3/api-docs",
            "/post-service/v3/api-docs",
            "/exercise-service/v3/api-docs",
            "/training-service/v3/api-docs",
            "/order-service/v3/api-docs",
            "/webjars/**",
            "/favicon.ico",
            "/auth/github/callback",
            "/auth/google/callback", "/auth/google/login",
            "/auth/resetPassword", "/auth/changePassword", "/auth/**");

    private static final List<String> TRAINER_LIST = List.of("/test/trainer",

            "/posts/create",
            "/posts/update/**",
            "/posts/trainer/**",
            "/posts/delete/**",

            "/exercises/create",
            "/exercises/update/**",
            "/exercises/trainer/**",
            "/exercises/delete/**",

            "/trainings/create",
            "/trainings/update/**",
            "/trainings/trainer/**",
            "/trainings/delete/**",

            "/orders/create",
            "/orders/trainer/**");

    private static final List<String> ADMIN_LIST = List.of("/test/admin",
            "/posts/admin/**",
            "/exercises/admin/**",
            "/users/admin/**",
            "/trainings/admin/**",
            "/orders/admin/**",
            "/invoices/**"
    );

    public Predicate<ServerHttpRequest> isWhitelisted() {
        return r -> AUTH_WHITELIST.stream().anyMatch(uri -> antPathMatcher.match(uri, r.getURI().getPath()));
    }

    public Predicate<ServerHttpRequest> isTrainer() {
        return r -> TRAINER_LIST.stream().anyMatch(uri -> antPathMatcher.match(uri, r.getURI().getPath()));
    }

    public Predicate<ServerHttpRequest> isAdmin() {
        return r -> ADMIN_LIST.stream().anyMatch(uri -> antPathMatcher.match(uri, r.getURI().getPath()));
    }

    public Predicate<ServerHttpRequest> isUser() {
        return r -> !isWhitelisted().test(r) && !isTrainer().test(r) && !isAdmin().test(r);
    }

    public Role getMinRole(ServerHttpRequest request) {
        if (isWhitelisted().test(request)) {
            return null;
        }
        if (isUser().test(request)) {
            return Role.ROLE_USER;
        }
        if (isTrainer().test(request)) {
            return Role.ROLE_TRAINER;
        }
        return Role.ROLE_ADMIN;
    }
}
