package com.example.userservice.exceptions;

import lombok.Getter;

@Getter
public class EmailAlreadyVerified extends RuntimeException {
    public EmailAlreadyVerified(String email) {
        super("Email " + email + " is already verified");
    }
}
