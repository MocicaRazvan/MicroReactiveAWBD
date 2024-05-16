package com.example.userservice.services.impl;

import com.example.commonmodule.enums.AuthProvider;
import com.example.userservice.dtos.auth.requests.CallbackBody;
import com.example.userservice.dtos.auth.response.AuthResponse;
import com.example.userservice.services.HandleUserProvider;
import com.example.userservice.services.OauthService;
import com.example.userservice.services.OauthUserInfoFactory;
import com.example.userservice.services.OauthUserInfoHandler;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Map;


@RequiredArgsConstructor
@Slf4j
public class OauthServiceImpl implements OauthService {

    protected final String clientId;
    protected final String clientSecret;
    protected final String tokenUri;
    protected final String userInfoUri;
    protected final String redirectUri;
    protected final AuthProvider provider;
    protected final WebClient.Builder webClient;
    protected final OauthUserInfoFactory oauthUserInfoFactory;
    protected final HandleUserProvider handleUserProvider;

    @Override
    public Mono<OAuth2AccessTokenResponse> exchangeToken(String code) {
        return webClient.build().post()
                .uri(tokenUri)
                .header("Accept", MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .bodyValue(Map.of(
                        OAuth2ParameterNames.CLIENT_ID, clientId,
                        OAuth2ParameterNames.CLIENT_SECRET, clientSecret,
                        OAuth2ParameterNames.CODE, code,
                        OAuth2ParameterNames.GRANT_TYPE, "authorization_code",
                        OAuth2ParameterNames.REDIRECT_URI, redirectUri
                ))
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(body -> {
                    Map<String, String> params = UriComponentsBuilder.fromUriString("/?" + body).build().getQueryParams().toSingleValueMap();
                    OAuth2AccessTokenResponse accessTokenResponse = OAuth2AccessTokenResponse.withToken(params.get("access_token"))
                            .tokenType(OAuth2AccessToken.TokenType.BEARER)
                            .expiresIn(Long.parseLong(params.getOrDefault("expires_in", "36000")))
                            .scopes(Collections.singleton("read:user"))
                            .build();
                    return Mono.just(accessTokenResponse);
                })
                .onErrorResume(e -> {
                    log.error("Error while exchanging token: {}", e.getMessage());
                    return Mono.error(e);
                });
    }


    @Override
    public Mono<AuthResponse> createOauth2JwtToken(OAuth2AccessTokenResponse accessTokenResponse) {
        OauthUserInfoHandler oauthUserInfoHandler = oauthUserInfoFactory.getOauthUserInfoHandler(provider, handleUserProvider);
        return webClient.build().get()
                .uri(userInfoUri)
                .header("Authorization", "Bearer " + accessTokenResponse.getAccessToken().getTokenValue())
                .header("Accept", MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                })
                .flatMap(userInfo -> oauthUserInfoHandler.handleUserInfo(accessTokenResponse, provider, userInfo)).onErrorResume(e -> {
                    log.error("Error while creating oauth2 jwt token");
                    log.error(e.getMessage());
                    return Mono.error(e);
                });
    }

    @Override
    public Mono<AuthResponse> handleProviderCallback(CallbackBody callbackBody) {
        return exchangeToken(callbackBody.getCode())
                .flatMap(this::createOauth2JwtToken);
    }
}
