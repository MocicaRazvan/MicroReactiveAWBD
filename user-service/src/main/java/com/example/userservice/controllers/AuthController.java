package com.example.userservice.controllers;

import com.example.userservice.dtos.auth.requests.CallbackBody;
import com.example.userservice.dtos.auth.requests.LoginRequest;
import com.example.userservice.dtos.auth.requests.RegisterRequest;
import com.example.userservice.dtos.auth.requests.TokenValidationRequest;
import com.example.userservice.dtos.auth.response.AuthResponse;
import com.example.userservice.dtos.auth.response.TokenValidationResponse;
import com.example.userservice.dtos.password.ForgotPasswordRequest;
import com.example.userservice.dtos.password.ResetPasswordRequest;
import com.example.userservice.services.AuthService;
import com.example.userservice.utils.PasswordResetUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.Duration;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication Controller")
@Slf4j
public class AuthController {
    private final AuthService authService;
    private final PasswordResetUtils passwordResetUtils;

    @PostMapping(value = "/register", produces = {MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest registerRequest
    ) {
        return authService.register(registerRequest)
                .map(resp -> ResponseEntity.ok()
                        .header(HttpHeaders.SET_COOKIE, createCookie(resp.getToken()).toString()).body(resp));
    }

    private ResponseCookie createCookie(String token) {
        return ResponseCookie.from("authToken", token)
                .httpOnly(true)
                .maxAge(Duration.ofDays(1))
                .path("/")
                .build();
    }

    @PostMapping(value = "/login", produces = {MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<AuthResponse>> login(
            @Valid @RequestBody LoginRequest loginRequest
    ) {
        return authService.login(loginRequest)
                .map(resp -> ResponseEntity.ok()
                        .header(HttpHeaders.SET_COOKIE, createCookie(resp.getToken()).toString()).body(resp));
    }

    @PostMapping(value = "/validateToken", produces = {MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<TokenValidationResponse>> validateToken(
            @Valid @RequestBody TokenValidationRequest tokenValidationRequest
    ) {
        return authService.validateToken(tokenValidationRequest)
                .map(ResponseEntity::ok);
    }

    @PostMapping(value = "/github/callback", produces = {MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<AuthResponse>> githubCallback(
            @Valid @RequestBody CallbackBody callbackBody
    ) {
        log.error(callbackBody.toString());
        return authService.handleGithubCallback(callbackBody)
                .map(resp -> ResponseEntity.ok()
                        .header(HttpHeaders.SET_COOKIE, createCookie(resp.getToken()).toString()).body(resp));
    }

    @PostMapping(value = "/resetPassword", produces = {MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<Void>> resetPassword(
            @Valid @RequestBody ForgotPasswordRequest forgotPasswordRequest
    ) {
        return passwordResetUtils.generatePasswordToken(forgotPasswordRequest)
                .then(Mono.just(ResponseEntity.ok().build()));

    }

    @PostMapping(value = "/changePassword", produces = {MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<Void>> changePassword(
            @Valid @RequestBody ResetPasswordRequest resetPasswordRequest
    ) {
        return passwordResetUtils.resetPassword(resetPasswordRequest)
                .then(Mono.just(ResponseEntity.ok().build()));
    
    }
}
//"/auth/resetPassword","/auth/changePassword"