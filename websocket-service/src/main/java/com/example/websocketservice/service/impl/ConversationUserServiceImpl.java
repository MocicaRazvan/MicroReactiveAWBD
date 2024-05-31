package com.example.websocketservice.service.impl;

import com.example.websocketservice.dtos.chatRoom.ChatRoomResponse;
import com.example.websocketservice.dtos.chatRoom.ChatRoomUserDto;
import com.example.websocketservice.dtos.user.ConnectUserPayload;
import com.example.websocketservice.dtos.user.ConversationUserPayload;
import com.example.websocketservice.dtos.user.ConversationUserResponse;
import com.example.websocketservice.enums.ConnectedStatus;
import com.example.websocketservice.exceptions.ConversationUserNotFound;
import com.example.websocketservice.exceptions.NoChatRoom;
import com.example.websocketservice.mappers.ConversationUserMapper;
import com.example.websocketservice.models.ConversationUser;
import com.example.websocketservice.repositories.ChatRoomRepository;
import com.example.websocketservice.repositories.ConversationUserRepository;
import com.example.websocketservice.service.ConversationUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ConversationUserServiceImpl implements ConversationUserService {
    private final ConversationUserMapper conversationUserMapper;
    private final ConversationUserRepository conversationUserRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatRoomRepository chatRoomRepository;

    @Override
    public ConversationUserResponse addUser(ConversationUserPayload conversationUserPayload) {
        return conversationUserRepository.findByEmail(conversationUserPayload.getEmail())
                .map(cur -> conversationUserMapper.copyFromPayload(conversationUserPayload, cur))
                .map(conversationUserRepository::save)
                .map(this::mapToResponseAndNotify)
                .orElseGet(() ->
                        conversationUserRepository.save(conversationUserMapper.fromPayloadToModel(conversationUserPayload))
                                .map(this::mapToResponseAndNotify)
                );
    }

    @Override
    public ConversationUserResponse changeUserConnectedStatus(ConnectedStatus connectedStatus, String email) {
        return conversationUserRepository.findByEmail(email)
                .map(cur -> {
                    cur.setConnectedStatus(connectedStatus);
                    return cur;
                }).map(conversationUserRepository::save)
                .map(this::mapToResponseAndNotify)
                .orElseGet(() -> addUser(ConversationUserPayload.builder().email(email)
                        .connectedStatus(connectedStatus).build()));
    }

    @Override
    public ConversationUser getUserByEmail(String email) {
        return conversationUserRepository.findByEmail(email)
                .orElseThrow(() -> new ConversationUserNotFound(email));
    }

    @Override
    public CompletableFuture<ConversationUser> getUserByEmailAsync(String email) {
        return getUserByEmail(email).
                map(CompletableFuture::completedFuture);
    }

    @Override
    public ConversationUser saveUser(ConversationUser conversationUser) {
        return conversationUserRepository.save(conversationUser);
    }

    @Override
    public List<ConversationUserResponse> getConnectedUsers() {
        return conversationUserRepository.findAllByConnectedStatusIs(ConnectedStatus.ONLINE)
                .stream()
                .map(conversationUserMapper::fromModelToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ConversationUserResponse changeUserChatRoom(ChatRoomUserDto chatRoomUserDto) {
        return getUserByEmail(chatRoomUserDto.getUserEmail())
                .map(u -> {
                    if (chatRoomUserDto.getChatId() == null) {
                        u.setConnectedChatRoom(null);
                        return u;
                    } else {
                        return chatRoomRepository.findById(chatRoomUserDto.getChatId())
                                .orElseThrow(() -> new NoChatRoom(chatRoomUserDto.getChatId()))
                                .map(c -> {
                                    u.setConnectedChatRoom(c);
                                    return u;
                                });
                    }
                }).map(conversationUserRepository::save)
                .map(this::mapToResponseAndNotify);
    }

    private ConversationUserResponse mapToResponseAndNotify(ConversationUser conversationUser) {
        notifyOtherUsers(conversationUser.getEmail());
        return conversationUserMapper.fromModelToResponse(conversationUser);
    }

    private void notifyOtherUsers(String senderEmail) {
        chatRoomRepository.findOthersEmailsBySenderEmail(senderEmail)
                .forEach(d -> messagingTemplate.convertAndSendToUser(d.getUserEmail(), "/chatRooms",
                        ChatRoomResponse.builder()
                                .id(d.getChatId())
                                .users(
                                        Stream.of(d.getUserEmail(), senderEmail)
                                                .map(this::getUserByEmail)
                                                .map(conversationUserMapper::fromModelToResponse)
                                                .collect(Collectors.toSet())
                                )
                                .build()));
    }


}
