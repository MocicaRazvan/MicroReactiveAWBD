package com.example.userservice.services;


import com.example.commonmodule.enums.AuthProvider;
import com.example.userservice.dtos.auth.response.AuthResponse;
import com.example.userservice.models.UserCustom;
import reactor.core.publisher.Mono;

public interface HandleUserProvider {

    Mono<AuthResponse> saveOrUpdateUserProvider(AuthProvider provider, UserCustom user);

    Mono<AuthResponse> generateResponse(UserCustom user, AuthProvider authProvider);
}
