package com.example.invoicesservice.exceptions;

public class ServiceUnreachable extends RuntimeException {
    public ServiceUnreachable(String message, Throwable cause) {
        super(message, cause);
    }
}
