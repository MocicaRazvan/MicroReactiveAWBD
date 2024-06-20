package com.example.websocketservice.service.impl;

import com.example.websocketservice.dtos.chatRoom.ChatRoomPayload;
import com.example.websocketservice.dtos.chatRoom.ChatRoomResponse;
import com.example.websocketservice.dtos.user.ConversationUserBase;
import com.example.websocketservice.enums.NotificationNotifyType;
import com.example.websocketservice.exceptions.MoreThenOneChatRoom;
import com.example.websocketservice.exceptions.SameUserChatRoom;
import com.example.websocketservice.exceptions.UserIsConnectedToTheRoom;
import com.example.websocketservice.exceptions.notFound.NoChatRoomFound;
import com.example.websocketservice.mappers.ChatRoomMapper;
import com.example.websocketservice.models.ChatRoom;
import com.example.websocketservice.models.ConversationUser;
import com.example.websocketservice.repositories.ChatMessageRepository;
import com.example.websocketservice.repositories.ChatRoomRepository;
import com.example.websocketservice.service.ChatMessageNotificationService;
import com.example.websocketservice.service.ChatRoomService;
import com.example.websocketservice.service.ConversationUserService;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.PessimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.LockAcquisitionException;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatRoomServiceImpl implements ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMapper chatRoomMapper;
    private final ConversationUserService conversationUserService;
    private final SimpMessagingTemplate messagingTemplate;
    private final Executor asyncExecutor;
    private final ChatMessageNotificationService chatMessageNotificationService;
    private final ChatMessageRepository chatMessageRepository;

    @Override
    public ChatRoomResponse createChatRoom(ChatRoomPayload chatRoomPayload) {
        Set<String> emails = chatRoomPayload.getUsers()
                .stream().map(ConversationUserBase::getEmail)
                .collect(Collectors.toSet());

        emails.forEach(conversationUserService::saveUserByEmailIfNotExist);

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

    // todo scoate transactional
//    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    @Override
    public List<ChatRoom> getRoomsByUsers(Set<String> emails) {
        var rooms = chatRoomRepository.findByUsers(emails
                , emails.size());
        log.error("Rooms: {}", rooms);
        return rooms;
    }

    // todo scoate transactional
//    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    @Override
    public List<ChatRoomResponse> getChatRooms(String email) {
        log.error("Email: {}", email);
        var initial = chatRoomRepository.findChatRoomsByUserEmail(email);
        log.error("Initial: {}", initial.toString());
        var rooms = initial
                .stream()
                .map(chatRoomMapper::fromModelToResponse)
                .collect(Collectors.toList());
        log.error("Rooms : {}", rooms.toString());
        return rooms;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.REPEATABLE_READ)
    @Retryable(
            retryFor = {OptimisticLockException.class,
                    PessimisticLockException.class,
                    CannotAcquireLockException.class,
                    JpaSystemException.class,
                    LockAcquisitionException.class,
                    ObjectOptimisticLockingFailureException.class,
                    CannotAcquireLockException.class},
            maxAttempts = 5,
            backoff = @Backoff(delay = 200, multiplier = 2, maxDelay = 1000))
    @Override
    public void deleteChatRoom(Long id, String senderEmail) {
        CompletableFuture<ChatRoom> chatRoomCompletableFuture = getChatRoomByIdAsync(id);
        CompletableFuture<ConversationUser> conversationUserCompletableFuture = conversationUserService.getUserByEmailAsync(senderEmail);

        CompletableFuture.allOf(chatRoomCompletableFuture, conversationUserCompletableFuture)
                .thenComposeAsync(v -> {
                    try {
                        ChatRoom chatRoom = chatRoomCompletableFuture.get();
                        ConversationUser conversationUser = conversationUserCompletableFuture.get();
                        chatRoom.getUsers().stream()
                                .filter(u -> !Objects.equals(u.getId(), conversationUser.getId())
                                        && u.getConnectedChatRoom() != null && u.getConnectedChatRoom().getId().equals(id)
                                )
                                .findFirst().ifPresent(u -> {
                                    throw new UserIsConnectedToTheRoom(u.getId());
                                });

                        List<String> receiverEmails = chatRoom.getUsers()
                                .stream().filter(u -> !u.getId().equals(conversationUser.getId()))
                                .map(ConversationUser::getEmail)
                                .toList();

                        return CompletableFuture.allOf(
//                                deleteMessagesByChatRoomId(id),
                                chatMessageNotificationService.notifyDeleteByReferenceId(id, receiverEmails),
                                deleteChatRoomById(id)
                        ).thenRunAsync(() -> {
                            notifyUsersModel(
                                    new HashSet<>(chatRoom.getUsers()),
                                    chatRoomMapper.fromModelToResponse(chatRoom),
                                    "/delete"
                            );
                        }, asyncExecutor);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }, asyncExecutor).join();
    }

    public ChatRoom getChatRoomById(Long id) {
        return chatRoomRepository.findById(id).orElseThrow(() -> new NoChatRoomFound(id));
    }

    public CompletableFuture<ChatRoom> getChatRoomByIdAsync(Long id) {
        return CompletableFuture.supplyAsync(() -> getChatRoomById(id), asyncExecutor);
    }


    public CompletableFuture<Void> deleteChatRoomById(Long id) {
        return CompletableFuture.runAsync(() -> chatRoomRepository.deleteById(id), asyncExecutor);
    }

    public CompletableFuture<Void> deleteMessagesByChatRoomId(Long chatRoomId) {
        return CompletableFuture.runAsync(() -> chatMessageRepository.deleteAllByChatRoomId(chatRoomId), asyncExecutor);
    }

    public void notifyUsers(Set<ConversationUserBase> users, ChatRoomResponse chatRoomResponse) {
        notifyUsers(users, chatRoomResponse, "");
    }

    public void notifyUsers(Set<ConversationUserBase> users, ChatRoomResponse chatRoomResponse, String path) {
        users.forEach(u -> messagingTemplate.convertAndSendToUser(u.getEmail(), "/chatRooms" + path, chatRoomResponse));
    }

    public void notifyUsersModel(Set<ConversationUser> users, ChatRoomResponse chatRoomResponse, String path) {
        users.forEach(u -> messagingTemplate.convertAndSendToUser(u.getEmail(), "/chatRooms" + path, chatRoomResponse));
    }


}
