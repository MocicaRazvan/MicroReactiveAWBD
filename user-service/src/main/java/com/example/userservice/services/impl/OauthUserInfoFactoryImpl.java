package com.example.userservice.services.impl;

import com.example.commonmodule.enums.AuthProvider;
import com.example.userservice.services.HandleUserProvider;
import com.example.userservice.services.OauthUserInfoFactory;
import com.example.userservice.services.OauthUserInfoHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;


@Component
public class OauthUserInfoFactoryImpl implements OauthUserInfoFactory {

    private final WebClient.Builder webClientBuilder;

    public OauthUserInfoFactoryImpl(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    @Override
    public OauthUserInfoHandler getOauthUserInfoHandler(AuthProvider provider, HandleUserProvider handleUserProvider) {
        switch (provider) {
            case GITHUB:
                return new GithubUserInfo(webClientBuilder, handleUserProvider);
            default:
                throw new IllegalArgumentException("Unsupported AuthProvider: " + provider);
        }
    }
}