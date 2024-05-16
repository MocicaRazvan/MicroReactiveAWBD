package com.example.commonmodule.exceptions.client;

import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
public class ThrowFallback extends RuntimeException {

    private final HttpStatus status = HttpStatus.SERVICE_UNAVAILABLE;
}
