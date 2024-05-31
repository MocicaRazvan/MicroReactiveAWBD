package com.example.websocketservice.exceptions;

public class SameUserChatRoom extends RuntimeException {

    public SameUserChatRoom() {
        super("Can't create chat room with same user with email.");
    }
}
