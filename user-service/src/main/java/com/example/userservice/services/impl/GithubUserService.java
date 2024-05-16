package com.example.userservice.services.impl;


import com.example.commonmodule.enums.AuthProvider;
import com.example.userservice.services.HandleUserProvider;
import com.example.userservice.services.OauthUserInfoFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class GithubUserService {

    @Value("${git.client.id}")
    private String clientId;
    @Value("${git.client.secret}")
    private String clientSecret;
    @Value("${git.redirect.uri}")
    private String redirectUri;
    private OauthServiceImpl oAuthService = null;

    private final OauthUserInfoFactory oauthUserInfoFactory;


    public OauthServiceImpl getOAuthService(WebClient.Builder webClient,
                                            HandleUserProvider handleUserProvider) {
        if (oAuthService == null) {
            oAuthService = new OauthServiceImpl(clientId, clientSecret, "https://github.com/login/oauth/access_token", "https://api.github.com/user", redirectUri, AuthProvider.GITHUB, webClient, oauthUserInfoFactory, handleUserProvider);
        }
        return oAuthService;
    }

}
