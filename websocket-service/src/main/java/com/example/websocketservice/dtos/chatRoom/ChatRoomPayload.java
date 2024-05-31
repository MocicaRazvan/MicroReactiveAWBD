package com.example.websocketservice.dtos.chatRoom;

import com.example.websocketservice.dtos.user.ConversationUserBase;
import com.example.websocketservice.utils.Transformable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ChatRoomPayload implements Transformable<ChatRoomPayload> {
    private Set<ConversationUserBase> users;

}
