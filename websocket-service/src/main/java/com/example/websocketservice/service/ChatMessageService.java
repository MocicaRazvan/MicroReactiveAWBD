package com.example.websocketservice.service;

import com.example.websocketservice.dtos.message.ChatMessagePayload;
import com.example.websocketservice.dtos.message.ChatMessageResponse;

import java.util.List;

public interface ChatMessageService {
    ChatMessageResponse sendMessage(ChatMessagePayload chatMessagePayload);


    List<ChatMessageResponse> getMessages(Long chatRoomId);
}
