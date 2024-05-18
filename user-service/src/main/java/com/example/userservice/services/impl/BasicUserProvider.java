package com.example.userservice.services.impl;

import com.example.commonmodule.enums.AuthProvider;
import com.example.userservice.dtos.auth.response.AuthResponse;
import com.example.userservice.jwt.JwtUtils;
import com.example.userservice.mappers.UserMapper;
import com.example.userservice.models.JwtToken;
import com.example.userservice.models.UserCustom;
import com.example.userservice.repositories.JwtTokenRepository;
import com.example.userservice.repositories.UserRepository;
import com.example.userservice.services.HandleUserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;


//@Service
@RequiredArgsConstructor
public class BasicUserProvider implements HandleUserProvider {

    protected final UserRepository userRepository;
    protected final JwtTokenRepository jwtTokenRepository;
    protected final JwtUtils jwtUtil;
    protected final UserMapper userMapper;

    public Mono<AuthResponse> saveOrUpdateUserProvider(AuthProvider provider, UserCustom user) {
        return userRepository.findByEmail(user.getEmail())
                .log()
                .flatMap(u -> generateResponse(u, u.getProvider()))
                .switchIfEmpty(userRepository.save(user)
                        .flatMap(u -> generateResponse(u, provider)));
    }

    public Mono<AuthResponse> generateResponse(UserCustom user, AuthProvider authProvider) {
        user.setProvider(authProvider);
        JwtToken jwtToken = JwtToken.builder()
                .userId(user.getId())
                .token(jwtUtil.generateToken(user))
                .revoked(false)
                .build();
        return jwtTokenRepository.save(jwtToken)
                .map(t -> userMapper.fromUserCustomToAuthResponse(user).map(
                        u -> {
                            u.setToken(jwtToken.getToken());
                            return u;
                        }
                ));

    }

}
