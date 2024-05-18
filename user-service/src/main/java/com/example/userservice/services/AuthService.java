package com.example.userservice.services;

import com.example.userservice.dtos.auth.requests.CallbackBody;
import com.example.userservice.dtos.auth.requests.TokenValidationRequest;
import com.example.userservice.dtos.auth.response.AuthResponse;
import com.example.userservice.dtos.auth.requests.LoginRequest;
import com.example.userservice.dtos.auth.requests.RegisterRequest;
import com.example.userservice.dtos.auth.response.TokenValidationResponse;
import reactor.core.publisher.Mono;

public interface AuthService {

    Mono<AuthResponse> register(RegisterRequest registerRequest);

    Mono<AuthResponse> login(LoginRequest loginRequest);

    Mono<TokenValidationResponse> validateToken(TokenValidationRequest tokenValidationRequest);

    Mono<AuthResponse> handleGithubCallback(CallbackBody callbackBody);

    Mono<AuthResponse> handleGoogleCallback(CallbackBody callbackBody, String codeVerifier);
}
