package com.example.userservice.services.impl;


import com.example.commonmodule.enums.AuthProvider;
import com.example.commonmodule.exceptions.common.UsernameNotFoundException;
import com.example.userservice.dtos.otp.OTPRequest;
import com.example.userservice.dtos.otp.ResetPasswordRequest;
import com.example.userservice.email.EmailUtils;
import com.example.userservice.enums.OTPType;
import com.example.userservice.exceptions.EmailAlreadyVerified;
import com.example.userservice.models.OTPToken;
import com.example.userservice.models.UserCustom;
import com.example.userservice.repositories.OTPTokenRepository;
import com.example.userservice.repositories.UserRepository;
import com.example.userservice.services.OTPTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class OTPTokenServiceImpl implements OTPTokenService {

    private final long expireInSeconds = 60 * 60 * 24;

    private final UserRepository userRepository;
    private final OTPTokenRepository OTPTokenRepository;
    private final EmailUtils emailUtils;
    private final PasswordEncoder passwordEncoder;

    @Value("${front.url}")
    private String frontUrl;

    public Mono<Void> generatePasswordToken(OTPRequest otpRequest) {
        return generateToken(otpRequest, OTPType.PASSWORD)
                .flatMap(tuple -> sendResetEmail(tuple.getT1().getEmail(), tuple.getT2().getToken()));
    }

    public Mono<Void> generateEmailVerificationToken(OTPRequest otpRequest) {
        return generateToken(otpRequest, OTPType.CONFIRM_EMAIL)
                .filter(t -> !t.getT1().isEmailVerified())
                .switchIfEmpty(Mono.error(new EmailAlreadyVerified(otpRequest.getEmail())))
                .flatMap(tuple -> sendEmailVerificationEmail(tuple.getT1().getEmail(), tuple.getT2().getToken(), tuple.getT1().getId()));
    }

    private Mono<Tuple2<UserCustom, OTPToken>> generateToken(OTPRequest otpRequest, OTPType type) {
        return userRepository.findByEmailAndProvider(otpRequest.getEmail(), AuthProvider.LOCAL)
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("User with email: " + otpRequest.getEmail() + " not found")))
                .flatMap(user -> OTPTokenRepository.save(
                        OTPToken.builder()
                                .token(UUID.randomUUID().toString())
                                .expiresInSeconds(expireInSeconds)
                                .type(type)
                                .userId(user.getId()).build()
                ).flatMap(pt -> Mono.just(Tuples.of(user, pt))
                ));
    }

    private Mono<Void> sendResetEmail(String email, String token) {
        String resetUrl = frontUrl + "/auth/reset-password?token=" + token + "&email=" + email;
        String emailContent = "<p>Click the link below to reset your password:</p>" +
                "<a href=\"" + resetUrl + "\">Reset Password</a>";
        return emailUtils.sendEmail(email, "Password Reset", emailContent);
    }

    private Mono<Void> sendEmailVerificationEmail(String email, String token, Long userId) {
        String confirmUrl = frontUrl + "/auth/confirm-email?token=" + token + "&email=" + email + "&userId=" + userId;
        String emailContent = "<p>Click the link below to confirm your email:</p>" +
                "<a href=\"" + confirmUrl + "\">Confirm Email</a>";
        return emailUtils.sendEmail(email, "Email Verification", emailContent);
    }

    public Mono<Void> confirmEmail(String email, String token) {
//        return userRepository.findByEmailAndProvider(email, AuthProvider.LOCAL)
//                .switchIfEmpty(Mono.error(new UsernameNotFoundException("User with email: " + email + " not found")))
//                .flatMap(userCustom -> OTPTokenRepository.findByUserIdAndTokenAndType(userCustom.getId(), token, OTPType.CONFIRM_EMAIL)
//                        .filter(prt -> prt.getToken().equals(token) &&
//                                prt.getCreatedAt().plusSeconds(prt.getExpiresInSeconds()).isAfter(LocalDateTime.now()))
//                        .switchIfEmpty(Mono.error(new UsernameNotFoundException("Invalid token")))
//                        .flatMap(vt -> {
//                            userCustom.setEmailVerified(true);
//                            return userRepository.save(userCustom)
//                                    .then(OTPTokenRepository.deleteAllByUserIdAndType(userCustom.getId(), OTPType.CONFIRM_EMAIL));
//                        }));

        return handleToken(email, token, AuthProvider.LOCAL, OTPType.CONFIRM_EMAIL, tuple -> {
            tuple.getT1().setEmailVerified(true);
            return userRepository.save(tuple.getT1())
                    .then(OTPTokenRepository.deleteAllByUserIdAndType(tuple.getT1().getId(), OTPType.CONFIRM_EMAIL));
        });

    }


    public Mono<Void> resetPassword(ResetPasswordRequest resetPasswordRequest) {

//        return userRepository.findByEmailAndProvider(resetPasswordRequest.getEmail(), AuthProvider.LOCAL)
//                .switchIfEmpty(Mono.error(new UsernameNotFoundException("User with email: " + resetPasswordRequest.getEmail() + " not found")))
//                .flatMap(userCustom -> OTPTokenRepository.findByUserIdAndTokenAndType(userCustom.getId(), resetPasswordRequest.getToken(), OTPType.PASSWORD)
//                        .filter(prt -> prt.getToken().equals(resetPasswordRequest.getToken()) &&
//                                prt.getCreatedAt().plusSeconds(prt.getExpiresInSeconds()).isAfter(LocalDateTime.now()))
//                        .switchIfEmpty(Mono.error(new UsernameNotFoundException("Invalid token")))
//                        .flatMap(vt -> {
//                            userCustom.setPassword(passwordEncoder.encode(resetPasswordRequest.getNewPassword()));
//                            return userRepository.save(userCustom)
//                                    .then(OTPTokenRepository.deleteAllByUserIdAndType(userCustom.getId(), OTPType.PASSWORD));
//                        }));

        return handleToken(resetPasswordRequest.getEmail(), resetPasswordRequest.getToken(), AuthProvider.LOCAL, OTPType.PASSWORD, tuple -> {
            tuple.getT1().setPassword(passwordEncoder.encode(resetPasswordRequest.getNewPassword()));
            return userRepository.save(tuple.getT1())
                    .then(OTPTokenRepository.deleteAllByUserIdAndType(tuple.getT1().getId(), OTPType.PASSWORD));
        });

    }

    private Mono<Void> handleToken(String email, String token, AuthProvider provider, OTPType type, Function<Tuple2<UserCustom, OTPToken>, Mono<Void>> callback) {
        return userRepository.findByEmailAndProvider(email, provider)
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("User with email: " + email + " not found")))
                .flatMap(userCustom -> OTPTokenRepository.findByUserIdAndTokenAndType(userCustom.getId(), token, type)
                        .filter(prt -> prt.getToken().equals(token) &&
                                prt.getCreatedAt().plusSeconds(prt.getExpiresInSeconds()).isAfter(LocalDateTime.now())
                        )
                        .switchIfEmpty(Mono.error(new UsernameNotFoundException("Invalid token")))
                        .map(t -> Tuples.of(userCustom, t))
                        .flatMap(callback)
                );


    }
}
