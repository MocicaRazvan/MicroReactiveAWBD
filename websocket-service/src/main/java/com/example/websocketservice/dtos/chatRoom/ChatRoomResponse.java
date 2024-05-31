package com.example.websocketservice.dtos.chatRoom;

import com.example.websocketservice.dtos.generic.IdResponse;
import com.example.websocketservice.dtos.user.ConversationUserBase;
import com.example.websocketservice.dtos.user.ConversationUserResponse;
import com.example.websocketservice.utils.Transformable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ChatRoomResponse extends IdResponse implements Transformable<ChatRoomResponse> {

    private Set<ConversationUserResponse> users;
}
