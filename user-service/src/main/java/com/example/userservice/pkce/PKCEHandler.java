package com.example.userservice.pkce;

import com.example.userservice.dtos.auth.requests.CallbackBody;
import com.example.userservice.dtos.auth.response.AuthResponse;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Mono;

import java.util.function.BiFunction;

public interface PKCEHandler {
    Mono<CodeVerifierResponse> handleAuthorizationCode(@RequestParam String state);

    Mono<AuthResponse> handleAuthResponse(
            CallbackBody callbackBody, BiFunction<CallbackBody, String, Mono<AuthResponse>> handleCallback
    );

}
