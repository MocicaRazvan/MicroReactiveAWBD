package com.example.websocketservice.service.impl;

import com.example.websocketservice.dtos.chatRoom.ChatRoomResponse;
import com.example.websocketservice.dtos.chatRoom.ChatRoomUserDto;
import com.example.websocketservice.dtos.user.ConversationUserPayload;
import com.example.websocketservice.dtos.user.ConversationUserResponse;
import com.example.websocketservice.enums.ConnectedStatus;
import com.example.websocketservice.exceptions.notFound.ConversationUserNotFound;
import com.example.websocketservice.exceptions.notFound.NoChatRoomFound;
import com.example.websocketservice.mappers.ConversationUserMapper;
import com.example.websocketservice.models.ConversationUser;
import com.example.websocketservice.repositories.ChatRoomRepository;
import com.example.websocketservice.repositories.ConversationUserRepository;
import com.example.websocketservice.service.ConversationUserService;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.PessimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.LockAcquisitionException;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConversationUserServiceImpl implements ConversationUserService {
    private final ConversationUserMapper conversationUserMapper;
    private final ConversationUserRepository conversationUserRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatRoomRepository chatRoomRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Retryable(
            retryFor = {OptimisticLockException.class,
                    PessimisticLockException.class,
                    CannotAcquireLockException.class,
                    JpaSystemException.class,
                    LockAcquisitionException.class},
            maxAttempts = 5,
            backoff = @Backoff(delay = 200, multiplier = 2, maxDelay = 1000))
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
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Retryable(
            retryFor = {OptimisticLockException.class,
                    PessimisticLockException.class,
                    CannotAcquireLockException.class,
                    JpaSystemException.class,
                    LockAcquisitionException.class},
            maxAttempts = 5,
            backoff = @Backoff(delay = 200, multiplier = 2, maxDelay = 1000))
    public ConversationUserResponse changeUserConnectedStatus(ConnectedStatus connectedStatus, String email) {
        return conversationUserRepository.findByEmail(email)
                .map(cur -> {
                    cur.setConnectedStatus(connectedStatus);
//                    cur.setConnectedChatRoom(null);
                    if (connectedStatus == ConnectedStatus.OFFLINE)
                        cur.setConnectedChatRoom(null);
                    return cur;
                }).map(conversationUserRepository::save)
                .map(this::mapToResponseAndNotify)
                .orElseGet(() -> addUser(ConversationUserPayload.builder().email(email)
                        .connectedStatus(connectedStatus).build()));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Retryable(
            retryFor = {OptimisticLockException.class,
                    PessimisticLockException.class,
                    CannotAcquireLockException.class,
                    JpaSystemException.class,
                    LockAcquisitionException.class},
            maxAttempts = 5,
            backoff = @Backoff(delay = 200, multiplier = 2, maxDelay = 1000))
    public ConversationUser getUserByEmail(String email) {
        return conversationUserRepository.findByEmail(email)
                .orElseThrow(() -> new ConversationUserNotFound(email));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Retryable(
            retryFor = {OptimisticLockException.class,
                    PessimisticLockException.class,
                    CannotAcquireLockException.class,
                    JpaSystemException.class,
                    LockAcquisitionException.class},
            maxAttempts = 5,
            backoff = @Backoff(delay = 200, multiplier = 2, maxDelay = 1000))
    public CompletableFuture<ConversationUser> getUserByEmailAsync(String email) {
        return getUserByEmail(email).
                map(CompletableFuture::completedFuture);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Retryable(
            retryFor = {OptimisticLockException.class,
                    PessimisticLockException.class,
                    CannotAcquireLockException.class,
                    JpaSystemException.class,
                    LockAcquisitionException.class},
            maxAttempts = 5,
            backoff = @Backoff(delay = 200, multiplier = 2, maxDelay = 1000))
    public ConversationUser saveUser(ConversationUser conversationUser) {
        return conversationUserRepository.save(conversationUser);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Retryable(
            retryFor = {OptimisticLockException.class,
                    PessimisticLockException.class,
                    CannotAcquireLockException.class,
                    JpaSystemException.class,
                    LockAcquisitionException.class},
            maxAttempts = 5,
            backoff = @Backoff(delay = 200, multiplier = 2, maxDelay = 1000))
    public ConversationUser saveUserByEmailIfNotExist(String email) {
        return conversationUserRepository.findByEmail(email)
                .orElseGet(() -> conversationUserRepository.save(ConversationUser.builder().email(email)
                        .connectedStatus(ConnectedStatus.OFFLINE)
                        .build()));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Retryable(
            retryFor = {OptimisticLockException.class,
                    PessimisticLockException.class,
                    CannotAcquireLockException.class,
                    JpaSystemException.class,
                    LockAcquisitionException.class},
            maxAttempts = 5,
            backoff = @Backoff(delay = 200, multiplier = 2, maxDelay = 1000))
    public List<ConversationUserResponse> getConnectedUsers() {
        return conversationUserRepository.findAllByConnectedStatusIs(ConnectedStatus.ONLINE)
                .stream()
                .map(conversationUserMapper::fromModelToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Retryable(
            retryFor = {OptimisticLockException.class,
                    PessimisticLockException.class,
                    CannotAcquireLockException.class,
                    JpaSystemException.class,
                    LockAcquisitionException.class},
            maxAttempts = 5,
            backoff = @Backoff(delay = 200, multiplier = 2, maxDelay = 1000))
    public ConversationUserResponse changeUserChatRoom(ChatRoomUserDto chatRoomUserDto) {
        return getUserByEmail(chatRoomUserDto.getUserEmail())
                .map(u -> {
                    if (chatRoomUserDto.getChatId() == null) {
                        u.setConnectedChatRoom(null);
                        return u;
                    } else {
                        return chatRoomRepository.findById(chatRoomUserDto.getChatId())
                                .orElseThrow(() -> new NoChatRoomFound(chatRoomUserDto.getChatId()))
                                .map(c -> {
                                    u.setConnectedChatRoom(c);
                                    u.setConnectedStatus(ConnectedStatus.ONLINE);
                                    return u;
                                });
                    }
                }).map(conversationUserRepository::save)
                .map(this::mapToResponseAndNotify);
    }

    private ConversationUserResponse mapToResponseAndNotify(ConversationUser conversationUser) {
        notifyOtherUsers(conversationUser.getEmail());
        log.error("Conversation user chat room: {}", conversationUser.getConnectedChatRoom());
        messagingTemplate.convertAndSendToUser(conversationUser.getEmail(), "/chat/changed",
                ConversationUserResponse.builder()
                        .id(conversationUser.getId())
                        .connectedChatRoom(conversationUser.getConnectedChatRoom() == null ? null :
                                ChatRoomResponse.builder()
                                        .id(conversationUser.getConnectedChatRoom().getId())
                                        .users(conversationUser.getConnectedChatRoom().getUsers()
                                                .stream()
                                                .map(conversationUserMapper::fromModelToResponse)
                                                .collect(Collectors.toSet()))
                                        .build())
                        .build());
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
