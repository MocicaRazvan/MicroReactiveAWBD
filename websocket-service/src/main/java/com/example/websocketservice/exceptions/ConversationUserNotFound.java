package com.example.websocketservice.exceptions;


public class ConversationUserNotFound extends RuntimeException {
    public String email;

    public ConversationUserNotFound(String email) {
        super("User with email " + email + " not found");
        this.email = email;
    }
}
