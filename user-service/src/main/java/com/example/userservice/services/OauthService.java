package com.example.userservice.services;


import com.example.userservice.dtos.auth.requests.CallbackBody;
import com.example.userservice.dtos.auth.response.AuthResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import reactor.core.publisher.Mono;

public interface OauthService {

    Mono<OAuth2AccessTokenResponse> exchangeToken(String code);

    Mono<AuthResponse> createOauth2JwtToken(OAuth2AccessTokenResponse accessTokenResponse);

    Mono<AuthResponse> handleProviderCallback(CallbackBody callbackBody);
}
