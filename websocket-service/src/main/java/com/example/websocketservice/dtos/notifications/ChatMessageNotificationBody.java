package com.example.websocketservice.dtos.notifications;

import com.example.websocketservice.dtos.generic.NotificationTemplateBody;
import com.example.websocketservice.enums.ChatMessageNotificationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
//@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ChatMessageNotificationBody extends NotificationTemplateBody<ChatMessageNotificationType> {
}
