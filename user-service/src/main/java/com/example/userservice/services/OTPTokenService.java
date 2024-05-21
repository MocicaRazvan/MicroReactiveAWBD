package com.example.userservice.services;

import com.example.userservice.dtos.otp.OTPRequest;
import com.example.userservice.dtos.otp.ResetPasswordRequest;
import reactor.core.publisher.Mono;

public interface OTPTokenService {
    Mono<Void> generatePasswordToken(OTPRequest OTPRequest);

    Mono<Void> resetPassword(ResetPasswordRequest resetPasswordRequest);

    Mono<Void> generateEmailVerificationToken(OTPRequest otpRequest);

    Mono<Void> confirmEmail(String email, String token);
}
