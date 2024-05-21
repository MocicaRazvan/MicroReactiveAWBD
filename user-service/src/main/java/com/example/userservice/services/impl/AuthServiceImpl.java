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


}
