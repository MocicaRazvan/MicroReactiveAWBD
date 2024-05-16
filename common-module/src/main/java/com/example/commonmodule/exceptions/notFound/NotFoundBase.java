package com.example.commonmodule.exceptions.notFound;

public abstract class NotFoundBase extends RuntimeException {
    public NotFoundBase(String message) {
        super(message);
    }
}
