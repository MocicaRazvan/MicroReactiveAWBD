package com.example.websocketservice.repositories;

import com.example.websocketservice.enums.ChatMessageNotificationType;
import com.example.websocketservice.models.ChatMessageNotification;
import com.example.websocketservice.models.ChatRoom;
import com.example.websocketservice.repositories.generic.NotificationTemplateRepository;


public interface ChatMessageNotificationRepository extends
        NotificationTemplateRepository<ChatRoom, ChatMessageNotificationType, ChatMessageNotification> {
}
