package com.example.websocketservice.service.impl;

import com.example.websocketservice.dtos.chatRoom.ChatRoomPayload;
import com.example.websocketservice.dtos.chatRoom.ChatRoomResponse;
import com.example.websocketservice.dtos.chatRoom.ChatRoomUserDto;
import com.example.websocketservice.dtos.user.ConversationUserBase;
import com.example.websocketservice.exceptions.MoreThenOneChatRoom;
import com.example.websocketservice.exceptions.SameUserChatRoom;
import com.example.websocketservice.mappers.ChatRoomMapper;
import com.example.websocketservice.models.ChatRoom;
import com.example.websocketservice.repositories.ChatRoomRepository;
import com.example.websocketservice.service.ChatRoomService;
import com.example.websocketservice.service.ConversationUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMapper chatRoomMapper;
    private final ConversationUserService conversationUserService;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public ChatRoomResponse createChatRoom(ChatRoomPayload chatRoomPayload) {
        Set<String> emails = chatRoomPayload.getUsers()
                .stream().map(ConversationUserBase::getEmail)
                .collect(Collectors.toSet());

        if (emails.size() == 1) {
            throw new SameUserChatRoom();
        }

        List<ChatRoom> rooms = getRoomsByUsers(emails);
        if (rooms.size() > 1) {
            throw new MoreThenOneChatRoom(emails);
        }
        if (rooms.isEmpty()) {
            return chatRoomMapper.fromPayloadToModel(chatRoomPayload)
                    .map(chatRoomRepository::save)
                    .map(c -> {
                        ChatRoomResponse cr = chatRoomMapper.fromModelToResponse(c);
                        notifyUsers(chatRoomPayload.getUsers(), cr);
                        return cr;
                    });
        }
        return chatRoomMapper.fromModelToResponse(rooms.getFirst())
                .map(cr -> {
                    notifyUsers(chatRoomPayload.getUsers(), cr);
                    return cr;
                });
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    @Override
    public List<ChatRoom> getRoomsByUsers(Set<String> emails) {
        return chatRoomRepository.findByUsers(emails
                , emails.size());
    }

    @Transactional
    @Override
    public List<ChatRoomResponse> getChatRooms(String email) {
        return chatRoomRepository.findChatRoomsByUserEmail(email)
                .stream()
                .map(chatRoomMapper::fromModelToResponse)
                .collect(Collectors.toList());
    }


    public void notifyUsers(Set<ConversationUserBase> users, ChatRoomResponse chatRoomResponse) {
        users.forEach(u -> messagingTemplate.convertAndSendToUser(u.getEmail(), "/chatRooms", chatRoomResponse));
    }


}
