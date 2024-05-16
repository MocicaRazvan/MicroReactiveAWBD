package com.example.userservice.repositories;

import com.example.userservice.models.PasswordResetToken;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PasswordResetTokenRepository extends R2dbcRepository<PasswordResetToken, Long> {
    Mono<Boolean> existsByUserId(long userId);

    Mono<PasswordResetToken> findByUserIdAndToken(long userId, String token);

    Mono<Void> deleteAllByUserId(long userId);
}
