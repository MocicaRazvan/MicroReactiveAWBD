package com.example.websocketservice.dtos.chatRoom;

import com.example.websocketservice.utils.Transformable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@SuperBuilder
public class ChatRoomUserDto implements Transformable<ChatRoomUserDto> {
    private Long chatId;
    private String userEmail;

    public ChatRoomUserDto(Long chatId, String userEmail) {
        this.chatId = chatId;
        this.userEmail = userEmail;
    }
}
