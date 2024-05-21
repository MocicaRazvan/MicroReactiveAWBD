package com.example.userservice.repositories;

import com.example.userservice.enums.OTPType;
import com.example.userservice.models.OTPToken;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface OTPTokenRepository extends R2dbcRepository<OTPToken, Long> {
    Mono<Boolean> existsByUserId(long userId);

    Mono<OTPToken> findByUserIdAndTokenAndType(long userId, String token, OTPType type);

    Mono<Void> deleteAllByUserIdAndType(long userId, OTPType type);
}
