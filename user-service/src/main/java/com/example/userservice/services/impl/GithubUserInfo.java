package com.example.userservice.services.impl;

import com.example.commonmodule.enums.AuthProvider;
import com.example.commonmodule.enums.Role;
import com.example.userservice.dtos.auth.response.AuthResponse;
import com.example.userservice.models.UserCustom;
import com.example.userservice.services.OauthUserInfoHandler;
import com.example.userservice.services.HandleUserProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;


@RequiredArgsConstructor
@Slf4j
public class GithubUserInfo implements OauthUserInfoHandler {

    private final WebClient.Builder webClient;
    private final HandleUserProvider handleUserProvider;

    @Override
    public Mono<AuthResponse> handleUserInfo(OAuth2AccessTokenResponse accessTokenResponse, AuthProvider provider, Map<String, Object> userInfo) {
        String name = userInfo.getOrDefault("name", "") != null ? userInfo.get("name").toString() : "";
        String[] nameParts = name.split(" ");
        String lastName = nameParts.length > 0 ? nameParts[0] : "";
        String firstName = nameParts.length > 1 ? nameParts[1] : "";


        UserCustom user = UserCustom.builder()
                .lastName(lastName)
                .firstName(firstName)
                .role(Role.ROLE_USER)
                .provider(provider)
                .image(userInfo.get("avatar_url").toString())
                .emailVerified(true)
                .build();

        String email = userInfo.getOrDefault("email", "") != null ? userInfo.get("email").toString() : "";

        if (email.isEmpty()) {
            return fetchPrimaryEmail(accessTokenResponse.getAccessToken().getTokenValue())
                    .flatMap(e -> {
                        user.setEmail(e);
                        return handleUserProvider.saveOrUpdateUserProvider(provider, user);
                    });
        }

        user.setEmail(email);
        return handleUserProvider.saveOrUpdateUserProvider(provider, user);
    }

    private Mono<String> fetchPrimaryEmail(String token) {
        return webClient.build().get()
                .uri("https://api.github.com/user/emails")
                .header("Authorization", "Bearer " + token)
                .header("Accept", MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {
                })
                .flatMap(emails -> {
                    for (Map<String, Object> emailData : emails) {
                        if (Boolean.TRUE.equals(emailData.get("primary"))) {
                            return Mono.justOrEmpty((String) emailData.get("email"));
                        }
                    }
                    return Mono.empty();
                })
                .onErrorResume(e -> {
                    log.error("Error fetching primary email: {}", e.getMessage());
                    return Mono.error(e);
                });
    }

}
