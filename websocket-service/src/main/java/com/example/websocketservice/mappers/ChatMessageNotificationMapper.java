package com.example.websocketservice.mappers;

import com.example.websocketservice.dtos.chatRoom.ChatRoomResponse;
import com.example.websocketservice.dtos.notifications.ChatMessageNotificationResponse;
import com.example.websocketservice.enums.ChatMessageNotificationType;
import com.example.websocketservice.mappers.generic.NotificationTemplateMapper;
import com.example.websocketservice.models.ChatMessageNotification;
import com.example.websocketservice.models.ChatRoom;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatMessageNotificationMapper
        extends NotificationTemplateMapper<ChatRoom, ChatRoomResponse, ChatMessageNotificationType,
        ChatMessageNotification, ChatMessageNotificationResponse> {

    private final ChatRoomMapper chatRoomMapper;
    private final ConversationUserMapper conversationUserMapper;

    @Override
    public ChatMessageNotificationResponse fromModelToResponse(ChatMessageNotification chatMessageNotification) {
        return ChatMessageNotificationResponse.builder()
                .id(chatMessageNotification.getId())
                .sender(conversationUserMapper.fromModelToResponse(chatMessageNotification.getSender()))
                .receiver(conversationUserMapper.fromModelToResponse(chatMessageNotification.getReceiver()))
                .type(chatMessageNotification.getType())
                .reference(chatRoomMapper.fromModelToResponse(chatMessageNotification.getReference()))
                .content(chatMessageNotification.getContent())
                .extraLink(chatMessageNotification.getExtraLink())
                .timestamp(chatMessageNotification.getTimestamp())
                .build();
    }


}
