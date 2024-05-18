package com.example.userservice.services;


import com.example.commonmodule.enums.AuthProvider;
import com.example.userservice.services.impl.OauthServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;
import java.util.function.Function;

@RequiredArgsConstructor
public class AbstractOauthProvider {

    private OauthServiceImpl oAuthService = null;

    private final OauthUserInfoFactory oauthUserInfoFactory;
    private final String clientId;
    private final String clientSecret;
    private final String redirectUri;
    private final String tokenUri;
    private final String userInfoUri;
    private final AuthProvider authProvider;
    private final Function<String, Map<String, String>> parseBody;


    public OauthServiceImpl getOAuthService(WebClient.Builder webClient,
                                            HandleUserProvider handleUserProvider) {
        if (oAuthService == null) {
            oAuthService = new OauthServiceImpl(clientId, clientSecret, tokenUri, userInfoUri, redirectUri,
                    authProvider, webClient, oauthUserInfoFactory, handleUserProvider, parseBody);
        }
        return oAuthService;
    }

}
