package com.example.userservice.services.impl;

import com.example.commonmodule.enums.AuthProvider;
import com.example.commonmodule.enums.Role;
import com.example.userservice.dtos.auth.requests.CallbackBody;
import com.example.userservice.dtos.auth.requests.TokenValidationRequest;
import com.example.userservice.dtos.auth.response.AuthResponse;
import com.example.userservice.dtos.auth.requests.LoginRequest;
import com.example.userservice.dtos.auth.requests.RegisterRequest;
import com.example.userservice.dtos.auth.response.TokenValidationResponse;
import com.example.userservice.exceptions.UserWithEmailExists;
import com.example.commonmodule.exceptions.common.UsernameNotFoundException;
import com.example.userservice.jwt.JwtUtils;
import com.example.userservice.mappers.UserMapper;
import com.example.userservice.models.UserCustom;
import com.example.userservice.repositories.JwtTokenRepository;
import com.example.userservice.repositories.UserRepository;
import com.example.userservice.services.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class AuthServiceImpl extends BasicUserProvider implements AuthService {


    private final WebClient.Builder webClient;
    private final PasswordEncoder passwordEncoder;
    private final GithubUserService githubUserService;
    private final GoogleUserService googleUserService;


    public AuthServiceImpl(UserRepository userRepository, JwtTokenRepository jwtTokenRepository, JwtUtils jwtUtil, UserMapper userMapper, WebClient.Builder webClient, PasswordEncoder passwordEncoder, GithubUserService githubUserService, GoogleUserService googleUserService) {
        super(userRepository, jwtTokenRepository, jwtUtil, userMapper);
        this.webClient = webClient;
        this.passwordEncoder = passwordEncoder;
        this.githubUserService = githubUserService;
        this.googleUserService = googleUserService;
    }

    @Override
    public Mono<AuthResponse> register(RegisterRequest registerRequest) {
        log.error(userMapper.fromRegisterRequestToUserCustom(registerRequest).toString());
        return userRepository.existsByEmail(registerRequest.getEmail())
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new UserWithEmailExists(registerRequest.getEmail()));
                    }
                    return userRepository.save(userMapper.fromRegisterRequestToUserCustom(registerRequest))
                            .flatMap(u -> generateResponse(u, AuthProvider.LOCAL));
                });
    }

    @Override
    public Mono<AuthResponse> login(LoginRequest loginRequest) {
        return userRepository.findByEmail(loginRequest.getEmail())
                .filter(u -> {
                    log.info("User Provider: {}", u.getProvider());
                    return u.getProvider().equals(AuthProvider.LOCAL);
                })
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("User with email: " + loginRequest.getEmail() + " not found")))
                .flatMap(u ->
                        revokeOldTokens(u).then(Mono.defer(() -> {
                            if (!passwordEncoder.matches(loginRequest.getPassword(), u.getPassword())) {
                                log.error("Password not match");
                                return Mono.error(new UsernameNotFoundException("User with email: " + loginRequest.getEmail() + " not found"));
                            }
                            return generateResponse(u, AuthProvider.LOCAL);
                        }))
                );
    }

    @Override
    public Mono<TokenValidationResponse> validateToken(TokenValidationRequest tokenValidationRequest) {

        return userRepository.findByEmail(jwtUtil.extractUsername(tokenValidationRequest.getToken()))
                .flatMap(u -> {
                    boolean minRoleMatch = isMinRoleMatch(tokenValidationRequest, u);
                    if (!minRoleMatch) {
                        return Mono.just(TokenValidationResponse.builder().valid(false).build());
                    }
                    return jwtUtil.isTokenValid(tokenValidationRequest.getToken(), u.getUsername())
                            .map(valid -> {
                                if (!valid) {
                                    return TokenValidationResponse.builder().valid(false).build();
                                }
                                return TokenValidationResponse.builder().valid(true).userId(u.getId()).build();

                            });
                })
                .defaultIfEmpty(TokenValidationResponse.builder().valid(false).build())
                .onErrorResume(e -> Mono.just(TokenValidationResponse.builder().valid(false).build()));
    }

    private boolean isMinRoleMatch(TokenValidationRequest tokenValidationRequest, UserCustom u) {
        boolean minRoleMatch;
        if (tokenValidationRequest.getMinRoleRequired().equals(Role.ROLE_USER)) {
            minRoleMatch = u.getRole().equals(Role.ROLE_USER) || u.getRole().equals(Role.ROLE_ADMIN) || u.getRole().equals(Role.ROLE_TRAINER);
        } else if (tokenValidationRequest.getMinRoleRequired().equals(Role.ROLE_TRAINER)) {
            minRoleMatch = u.getRole().equals(Role.ROLE_TRAINER) || u.getRole().equals(Role.ROLE_ADMIN);
        } else {
            minRoleMatch = u.getRole().equals(Role.ROLE_ADMIN);
        }
        return minRoleMatch;
    }


//    private Mono<AuthResponse> generateResponse(UserCustom user, AuthProvider authProvider) {
//        user.setProvider(authProvider);
//        JwtToken jwtToken = JwtToken.builder()
//                .userId(user.getId())
//                .token(jwtUtil.generateToken(user))
//                .revoked(false)
//                .build();
//        return jwtTokenRepository.save(jwtToken)
//                .map(t -> userMapper.fromUserCustomToAuthResponse(user).map(
//                        u -> {
//                            u.setToken(jwtToken.getToken());
//                            return u;
//                        }
//                ));
//
//    }

    private Mono<Void> revokeOldTokens(UserCustom user) {
        return jwtTokenRepository.findAllByUserId(user.getId())
                .flatMap(t -> {
                    t.setRevoked(true);
                    return jwtTokenRepository.save(t);
                })
                .then();

    }


    @Override
    public Mono<AuthResponse> handleGithubCallback(CallbackBody callbackBody) {
        return githubUserService.getOAuthService(webClient, this).handleProviderCallback(callbackBody, null);
    }

    @Override
    public Mono<AuthResponse> handleGoogleCallback(CallbackBody callbackBody, String codeVerifier) {
        return googleUserService.getOAuthService(webClient, this).handleProviderCallback(callbackBody, codeVerifier);
    }

    // make it generic and then call the func with the provider and the uri
    // baga astea in alta clasa sa fie mai ok si sa nu fie atat de multa logica in service
//    @Override
//    public Mono<AuthResponse> handleGithubCallback(CallbackBody callbackBody) {
//        return exchangeToken(callbackBody.getCode(), gitClientId, gitClientSecret, "https://github.com/login/oauth/access_token")
//                .flatMap(accessTokenResponse -> createOauth2JwtToken(accessTokenResponse, AuthProvider.GITHUB, "https://api.github.com/user", gitUserInfoResponse));
//    }
//
//
//    private Mono<OAuth2AccessTokenResponse> exchangeToken(String code, String clientId, String clientSecret, String tokenUri) {
//        return webClient.build().post()
//                .uri(tokenUri)
//                .header("Accept", MediaType.APPLICATION_FORM_URLENCODED_VALUE)
//                .bodyValue(Map.of(
//                        OAuth2ParameterNames.CLIENT_ID, clientId,
//                        OAuth2ParameterNames.CLIENT_SECRET, clientSecret,
//                        OAuth2ParameterNames.CODE, code,
//                        OAuth2ParameterNames.GRANT_TYPE, "authorization_code",
//                        OAuth2ParameterNames.REDIRECT_URI, gitRedirectUri
//                ))
//                .retrieve()
//                .bodyToMono(String.class)
//                .flatMap(body -> {
//                    Map<String, String> params = UriComponentsBuilder.fromUriString("/?" + body).build().getQueryParams().toSingleValueMap();
//                    OAuth2AccessTokenResponse accessTokenResponse = OAuth2AccessTokenResponse.withToken(params.get("access_token"))
//                            .tokenType(OAuth2AccessToken.TokenType.BEARER)
//                            .expiresIn(Long.parseLong(params.getOrDefault("expires_in", "36000")))
//                            .scopes(Collections.singleton("read:user"))
//                            .build();
//                    return Mono.just(accessTokenResponse);
//                })
//                .onErrorResume(e -> {
//                    log.error("Error while exchanging token: {}", e.getMessage());
//                    return Mono.error(e);
//                });
//    }
//
//    private final OauthUserInfoHandler gitUserInfoResponse = (OAuth2AccessTokenResponse accessTokenResponse, AuthProvider provider, Map<String, Object> userInfo) -> {
//        String name = userInfo.getOrDefault("name", "") != null ? userInfo.get("name").toString() : "";
//        String[] nameParts = name.split(" ");
//        String lastName = nameParts.length > 0 ? nameParts[0] : "";
//        String firstName = nameParts.length > 1 ? nameParts[1] : "";
//
//
//        UserCustom user = UserCustom.builder()
//                .lastName(lastName)
//                .firstName(firstName)
//                .role(Role.ROLE_USER)
//                .provider(provider)
//                .image(userInfo.get("avatar_url").toString())
//                .build();
//
//        String email = userInfo.getOrDefault("email", "") != null ? userInfo.get("email").toString() : "";
//
//        if (email.isEmpty()) {
//            return fetchPrimaryEmail(accessTokenResponse.getAccessToken().getTokenValue())
//                    .flatMap(e -> {
//                        user.setEmail(e);
//                        return saveOrUpdateUserProvider(provider, user);
//                    });
//        }
//
//        user.setEmail(email);
//        return saveOrUpdateUserProvider(provider, user);
//    };
//
//    private Mono<AuthResponse> createOauth2JwtToken(OAuth2AccessTokenResponse accessTokenResponse, AuthProvider provider, String userInfoUri, OauthUserInfoHandler oauthUserInfoHandler) {
//        return webClient.build().get()
//                .uri(userInfoUri)
//                .header("Authorization", "Bearer " + accessTokenResponse.getAccessToken().getTokenValue())
//                .header("Accept", MediaType.APPLICATION_JSON_VALUE)
//                .retrieve()
//                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
//                })
//                .flatMap(userInfo -> oauthUserInfoHandler.handleUserInfo(accessTokenResponse, provider, userInfo)).onErrorResume(e -> {
//                    log.error("Error while creating oauth2 jwt token");
//                    log.error(e.getMessage());
//                    return Mono.error(e);
//                });
//    }
//
//
//    public Mono<AuthResponse> saveOrUpdateUserProvider(AuthProvider provider, UserCustom user) {
//        return userRepository.findByEmail(user.getEmail())
//                .log()
//                .flatMap(u -> generateResponse(u, provider))
//                .switchIfEmpty(userRepository.save(user)
//                        .flatMap(u -> generateResponse(u, provider)));
//    }
//
//    private Mono<String> fetchPrimaryEmail(String token) {
//        return webClient.build().get()
//                .uri("https://api.github.com/user/emails")
//                .header("Authorization", "Bearer " + token)
//                .header("Accept", MediaType.APPLICATION_JSON_VALUE)
//                .retrieve()
//                .bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {
//                })
//                .flatMap(emails -> {
//                    for (Map<String, Object> emailData : emails) {
//                        if (Boolean.TRUE.equals(emailData.get("primary"))) {
//                            return Mono.justOrEmpty((String) emailData.get("email"));
//                        }
//                    }
//                    return Mono.empty();
//                })
//                .onErrorResume(e -> {
//                    log.error("Error fetching primary email: {}", e.getMessage());
//                    return Mono.error(e);
//                });
//    }

}
