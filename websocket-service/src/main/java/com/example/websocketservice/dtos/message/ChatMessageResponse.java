package com.example.websocketservice.dtos.message;

import com.example.websocketservice.dtos.chatRoom.ChatRoomResponse;
import com.example.websocketservice.dtos.generic.IdResponse;
import com.example.websocketservice.dtos.user.ConversationUserResponse;
import com.example.websocketservice.utils.Transformable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ChatMessageResponse extends IdResponse implements Transformable<ChatMessageResponse> {
    private ConversationUserResponse sender;
    private ConversationUserResponse receiver;
    private ChatRoomResponse chatRoom;
    private String content;
    private LocalDateTime timestamp;
}
