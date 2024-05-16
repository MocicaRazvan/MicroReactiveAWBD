package com.example.commonmodule.utils;

import com.example.commonmodule.exceptions.notFound.AuthHeaderNotFound;
import org.springframework.web.server.ServerWebExchange;

import java.util.Optional;

public class RequestsUtils {
    public static final String AUTH_HEADER = "x-auth-user-id";

    public String extractAuthUser(ServerWebExchange exchange) {
        return Optional.ofNullable(
                exchange.getRequest().getHeaders().getFirst(AUTH_HEADER)
        ).orElseThrow(AuthHeaderNotFound::new);
    }
}
