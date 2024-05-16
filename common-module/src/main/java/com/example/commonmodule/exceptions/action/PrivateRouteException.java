package com.example.commonmodule.exceptions.action;

public class PrivateRouteException extends RuntimeException {
    public PrivateRouteException() {
        super("Not allowed!");
    }
}
