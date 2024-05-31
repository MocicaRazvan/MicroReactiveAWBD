package com.example.websocketservice.mappers;


import com.example.websocketservice.dtos.chatRoom.ChatRoomResponse;
import com.example.websocketservice.dtos.user.ConversationUserPayload;
import com.example.websocketservice.dtos.user.ConversationUserResponse;
import com.example.websocketservice.models.ChatRoom;
import com.example.websocketservice.models.ConversationUser;
import com.example.websocketservice.repositories.ChatRoomRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class ConversationUserMapper {

    @Autowired
    private ChatRoomRepository chatRoomRepository;


    public abstract ConversationUser fromPayloadToModel(ConversationUserPayload conversationUserPayload);

    @Mapping(target = "connectedChatRoom", ignore = true)
    public abstract ConversationUserResponse _fromModelToResponse(ConversationUser conversationUser);

    public ConversationUser copyFromPayload(ConversationUserPayload conversationUserPayload, ConversationUser conversationUser) {
        ChatRoom chatRoom = conversationUserPayload.getConnectedChatRoomId() == null ? null :
                chatRoomRepository.findById(conversationUserPayload.getConnectedChatRoomId()).orElse(null);
        conversationUser.setConnectedChatRoom(chatRoom);
        conversationUser.setConnectedStatus(conversationUserPayload.getConnectedStatus());
        return conversationUser;
    }

    public ConversationUserResponse fromModelToResponse(ConversationUser conversationUser) {
        return conversationUser.map(this::_fromModelToResponse)
                .map(cur -> {
                    cur.setConnectedChatRoom(
                            conversationUser.getConnectedChatRoom() == null ? null :
                                    ChatRoomResponse.builder()
                                            .id(conversationUser.getConnectedChatRoom().getId())
                                            .users(
                                                    conversationUser.getConnectedChatRoom().getUsers()
                                                            .stream()
                                                            .map(this::_fromModelToResponse)
                                                            .collect(java.util.stream.Collectors.toSet())
                                            )
                                            .build()
                    );
                    return cur;
                });
    }
}
