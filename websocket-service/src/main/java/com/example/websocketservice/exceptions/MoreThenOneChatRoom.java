package com.example.websocketservice.exceptions;

import com.example.websocketservice.dtos.user.ConversationUserBase;

import java.util.Set;

public class MoreThenOneChatRoom extends RuntimeException {
    public Set<String> users;


    public MoreThenOneChatRoom(Set<String> users) {
        super("More then one chat room found for users: " + users);
        this.users = users;
    }
}
