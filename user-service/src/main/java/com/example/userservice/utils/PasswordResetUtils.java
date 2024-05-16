package com.example.userservice.utils;


import com.example.commonmodule.enums.AuthProvider;
import com.example.commonmodule.exceptions.common.UsernameNotFoundException;
import com.example.userservice.dtos.password.ForgotPasswordRequest;
import com.example.userservice.dtos.password.ResetPasswordRequest;
import com.example.userservice.email.EmailUtils;
import com.example.userservice.models.PasswordResetToken;
import com.example.userservice.repositories.PasswordResetTokenRepository;
import com.example.userservice.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.swing.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PasswordResetUtils {

    private final long expireInSeconds = 60 * 60 * 24;

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailUtils emailUtils;
    private final PasswordEncoder passwordEncoder;

    public Mono<Void> generatePasswordToken(ForgotPasswordRequest forgotPasswordRequest) {
        return userRepository.findByEmailAndProvider(forgotPasswordRequest.getEmail(), AuthProvider.LOCAL)
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("User with email: " + forgotPasswordRequest.getEmail() + " not found")))
                .flatMap(user -> passwordResetTokenRepository.save(
                                PasswordResetToken.builder()
                                        .token(UUID.randomUUID().toString())
                                        .expiresInSeconds(expireInSeconds)
                                        .userId(user.getId()).build()


                        ).flatMap(pt -> sendResetEmail(forgotPasswordRequest.getEmail(), pt.getToken()))
                );
    }

    private Mono<Void> sendResetEmail(String email, String token) {
        String resetUrl = "http://localhost:3000/resetPassword?token=" + token;
        String emailContent = "<p>Click the link below to reset your password:</p>" +
                "<a href=\"" + resetUrl + "\">Reset Password</a>";
        return emailUtils.sendEmail(email, "Password Reset", emailContent);
    }

    public Mono<Void> resetPassword(ResetPasswordRequest resetPasswordRequest) {

        return userRepository.findByEmailAndProvider(resetPasswordRequest.getEmail(), AuthProvider.LOCAL)
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("User with email: " + resetPasswordRequest.getEmail() + " not found")))
                .flatMap(userCustom -> passwordResetTokenRepository.findByUserIdAndToken(userCustom.getId(), resetPasswordRequest.getToken())
                        .filter(prt -> prt.getToken().equals(resetPasswordRequest.getToken()) &&
                                prt.getCreatedAt().plusSeconds(prt.getExpiresInSeconds()).isAfter(LocalDateTime.now()))
                        .switchIfEmpty(Mono.error(new UsernameNotFoundException("Invalid token")))
                        .flatMap(vt -> {
                            userCustom.setPassword(passwordEncoder.encode(resetPasswordRequest.getNewPassword()));
                            return userRepository.save(userCustom)
                                    .then(passwordResetTokenRepository.deleteAllByUserId(userCustom.getId()));
                        }));

    }
}
