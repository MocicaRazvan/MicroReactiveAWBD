package com.example.websocketservice.service;

import com.example.websocketservice.dtos.chatRoom.ChatRoomResponse;
import com.example.websocketservice.dtos.notifications.ChatMessageNotificationBody;
import com.example.websocketservice.dtos.notifications.ChatMessageNotificationResponse;
import com.example.websocketservice.enums.ChatMessageNotificationType;
import com.example.websocketservice.service.generic.NotificationTemplateService;

public interface ChatMessageNotificationService extends
        NotificationTemplateService
                <ChatRoomResponse, ChatMessageNotificationType, ChatMessageNotificationBody, ChatMessageNotificationResponse> {
}
