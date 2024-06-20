package com.example.websocketservice.service;

import com.example.websocketservice.dtos.message.ChatMessagePayload;
import com.example.websocketservice.dtos.message.ChatMessageResponse;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface ChatMessageService {
    ChatMessageResponse sendMessage(ChatMessagePayload chatMessagePayload);

    List<ChatMessageResponse> getMessages(Long chatRoomId);

}
