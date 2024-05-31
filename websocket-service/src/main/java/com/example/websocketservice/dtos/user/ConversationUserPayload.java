package com.example.websocketservice.dtos.user;

import com.example.websocketservice.enums.ConnectedStatus;
import com.example.websocketservice.utils.Transformable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.Data;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ConversationUserPayload extends ConversationUserBase
        implements Transformable<ConversationUserPayload> {
    private Long connectedChatRoomId;
}
