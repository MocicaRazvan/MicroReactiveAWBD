package com.example.userservice.exceptions;

public class StateNotFound extends RuntimeException {
    public StateNotFound() {
        super("State not found");
    }
}
