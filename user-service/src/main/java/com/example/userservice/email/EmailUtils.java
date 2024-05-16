package com.example.userservice.email;

import reactor.core.publisher.Mono;

public interface EmailUtils {

    Mono<Void> sendEmail(String to, String subject, String content);
}
