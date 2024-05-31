package com.example.websocketservice.mappers;

import com.example.websocketservice.dtos.chatRoom.ChatRoomResponse;
import com.example.websocketservice.dtos.message.ChatMessagePayload;
import com.example.websocketservice.dtos.message.ChatMessageResponse;
import com.example.websocketservice.models.ChatMessage;
import com.example.websocketservice.models.ChatRoom;
import com.example.websocketservice.service.ChatRoomService;
import com.example.websocketservice.service.ConversationUserService;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
public abstract class ChatMessageMapper {

    @Autowired
    private ConversationUserService conversationUserService;
    @Autowired
    private ChatRoomService chatRoomService;

    public ChatMessage fromPayloadToModel(ChatMessagePayload chatMessagePayload) {
        return ChatMessage.builder()
                .sender(conversationUserService.getUserByEmail(chatMessagePayload.getSenderEmail()))
                .receiver(conversationUserService.getUserByEmail(chatMessagePayload.getReceiverEmail()))
                .content(chatMessagePayload.getContent())
                .build();
    }

    public abstract ChatMessageResponse fromModelToResponse(ChatMessage chatMessage);

}
