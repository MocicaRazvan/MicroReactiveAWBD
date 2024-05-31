package com.example.websocketservice.mappers;

import com.example.websocketservice.dtos.chatRoom.ChatRoomResponse;
import com.example.websocketservice.dtos.notifications.ChatMessageNotificationBody;
import com.example.websocketservice.dtos.notifications.ChatMessageNotificationResponse;
import com.example.websocketservice.enums.ChatMessageNotificationType;
import com.example.websocketservice.mappers.generic.NotificationTemplateMapper;
import com.example.websocketservice.models.ChatMessageNotification;
import com.example.websocketservice.models.ChatRoom;

public abstract class ChatMessageNotificationMapper
        extends NotificationTemplateMapper<ChatRoom, ChatRoomResponse, ChatMessageNotificationType,
        ChatMessageNotification, ChatMessageNotificationBody, ChatMessageNotificationResponse> {
}
