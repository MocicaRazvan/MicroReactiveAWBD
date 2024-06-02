package com.example.websocketservice.service;

import com.example.websocketservice.dtos.chatRoom.ChatRoomResponse;
import com.example.websocketservice.dtos.chatRoom.ChatRoomUserDto;
import com.example.websocketservice.dtos.user.ConversationUserPayload;
import com.example.websocketservice.dtos.user.ConversationUserResponse;
import com.example.websocketservice.enums.ConnectedStatus;
import com.example.websocketservice.models.ConversationUser;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface ConversationUserService {

    ConversationUserResponse addUser(ConversationUserPayload conversationUserPayload);

    ConversationUserResponse changeUserConnectedStatus(ConnectedStatus connectedStatus, String email);

    ConversationUser getUserByEmail(String email);

    CompletableFuture<ConversationUser> getUserByEmailAsync(String email);

    ConversationUser saveUser(ConversationUser conversationUser);

    ConversationUser saveUserByEmailIfNotExist(String email);

    List<ConversationUserResponse> getConnectedUsers();

    ConversationUserResponse changeUserChatRoom(ChatRoomUserDto chatRoomUserDto);
}
