package com.example.websocketservice.exceptions;

import com.example.websocketservice.dtos.user.ConversationUserBase;

import java.util.Set;

public class NoChatRoom extends RuntimeException {
    public Set<String> users;
    public Long id;


    public NoChatRoom(Set<String> users) {
        super("No chat room found for users: " + users);
        this.users = users;
    }

    public NoChatRoom(Long id) {
        super("No chat room found for id: " + id);
        this.id = id;
    }
}