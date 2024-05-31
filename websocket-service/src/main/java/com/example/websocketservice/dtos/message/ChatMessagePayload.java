package com.example.websocketservice.dtos.message;

import com.example.websocketservice.utils.Transformable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ChatMessagePayload implements Transformable<ChatMessagePayload> {

    private String senderEmail;
    private String receiverEmail;
    private Long chatRoomId;
    private String content;
}
