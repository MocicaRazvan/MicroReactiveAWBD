package com.example.websocketservice.models;

import com.example.websocketservice.enums.ChatMessageNotificationType;
import com.example.websocketservice.models.generic.NotificationTemplate;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
//@AllArgsConstructor
@SuperBuilder
@Entity
public class ChatMessageNotification extends NotificationTemplate<ChatRoom, ChatMessageNotificationType> {
}
