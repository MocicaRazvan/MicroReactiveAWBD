package com.example.userservice.exceptions;

public class UserWithEmailExists extends RuntimeException {
    public UserWithEmailExists(String email) {
        super("User with email " + email + " already exists");
    }
}
