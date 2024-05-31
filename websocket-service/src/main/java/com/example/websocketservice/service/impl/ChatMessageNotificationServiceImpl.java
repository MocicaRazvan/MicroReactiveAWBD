package com.example.websocketservice.service.impl;

import com.example.websocketservice.dtos.chatRoom.ChatRoomResponse;
import com.example.websocketservice.dtos.notifications.ChatMessageNotificationBody;
import com.example.websocketservice.dtos.notifications.ChatMessageNotificationResponse;
import com.example.websocketservice.enums.ChatMessageNotificationType;
import com.example.websocketservice.mappers.ChatMessageNotificationMapper;
import com.example.websocketservice.mappers.ChatRoomMapper;
import com.example.websocketservice.models.ChatMessageNotification;
import com.example.websocketservice.models.ChatRoom;
import com.example.websocketservice.models.ConversationUser;
import com.example.websocketservice.repositories.ChatMessageNotificationRepository;
import com.example.websocketservice.repositories.ChatRoomRepository;
import com.example.websocketservice.service.ChatMessageNotificationService;
import com.example.websocketservice.service.ConversationUserService;
import com.example.websocketservice.service.generic.impl.NotificationTemplateServiceImpl;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executor;


@Service
public class ChatMessageNotificationServiceImpl
        extends NotificationTemplateServiceImpl<
        ChatRoom, ChatRoomResponse, ChatMessageNotificationType, ChatMessageNotification,
        ChatMessageNotificationBody, ChatMessageNotificationResponse, ChatRoomRepository,
        ChatMessageNotificationRepository, ChatMessageNotificationMapper
        >
        implements ChatMessageNotificationService {
    public ChatMessageNotificationServiceImpl(ChatRoomRepository referenceRepository, ConversationUserService conversationUserService, Executor asyncExecutor,
                                              ChatMessageNotificationRepository notificationTemplateRepository, ChatMessageNotificationMapper notificationTemplateMapper,
                                              SimpMessagingTemplate messagingTemplate) {
        super(referenceRepository, conversationUserService, "chatRoom", "chatMessageNotification", asyncExecutor, notificationTemplateRepository, notificationTemplateMapper, messagingTemplate);
    }

    @Override
    protected ChatMessageNotification createModelInstance(ConversationUser sender, ConversationUser receiver, ChatMessageNotificationType type, ChatRoom reference, String content, String extraLink) {
        return ChatMessageNotification.builder()
                .sender(sender)
                .receiver(receiver)
                .type(type)
                .reference(reference)
                .content(content)
                .extraLink(extraLink)
                .build();
    }
}
