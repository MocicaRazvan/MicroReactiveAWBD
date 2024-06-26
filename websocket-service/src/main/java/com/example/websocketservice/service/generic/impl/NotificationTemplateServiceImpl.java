package com.example.websocketservice.service.generic.impl;

import com.example.websocketservice.dtos.generic.IdResponse;
import com.example.websocketservice.dtos.generic.NotificationTemplateBody;
import com.example.websocketservice.dtos.generic.NotificationTemplateResponse;
import com.example.websocketservice.enums.NotificationNotifyType;
import com.example.websocketservice.exceptions.notFound.EntityNotFound;
import com.example.websocketservice.mappers.generic.NotificationTemplateMapper;
import com.example.websocketservice.models.ConversationUser;
import com.example.websocketservice.models.generic.IdGenerated;
import com.example.websocketservice.models.generic.NotificationTemplate;
import com.example.websocketservice.repositories.generic.IdGeneratedRepository;
import com.example.websocketservice.repositories.generic.NotificationTemplateRepository;
import com.example.websocketservice.service.ConversationUserService;
import com.example.websocketservice.service.generic.NotificationTemplateService;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.PessimisticLockException;
import lombok.RequiredArgsConstructor;
import org.hibernate.exception.LockAcquisitionException;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@RequiredArgsConstructor

public abstract class NotificationTemplateServiceImpl<R extends IdGenerated, RRESP extends IdResponse, E extends Enum<E>,
        MODEL extends NotificationTemplate<R, E>, BODY extends NotificationTemplateBody<E>, RESPONSE extends NotificationTemplateResponse<RRESP, E>,
        RREPO extends IdGeneratedRepository<R>,
        MREOP extends NotificationTemplateRepository<R, E, MODEL>,
        MMAP extends NotificationTemplateMapper<R, RRESP, E, MODEL, RESPONSE>> implements NotificationTemplateService<RRESP, E, BODY, RESPONSE> {

    private final RREPO referenceRepository;
    private final ConversationUserService conversationUserService;
    private final String referenceName;
    private final String notificationName;
    private final Executor asyncExecutor;
    private final MREOP notificationTemplateRepository;
    private final MMAP notificationTemplateMapper;
    private final SimpMessagingTemplate messagingTemplate;


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public RESPONSE saveNotification(BODY body) {
        return fromBodyToModel(body)
                .thenApplyAsync(model -> {
                            RESPONSE response = notificationTemplateMapper.fromModelToResponse(notificationTemplateRepository.save(model));
                            notifyReceiver(response, NotificationNotifyType.ADDED);
                            return response;
                        },
                        asyncExecutor)
                .join();
    }

    @Override
    public void deleteById(Long id) {
        MODEL m = getNotificationById(id);
        notificationTemplateRepository.delete(m);
        // todo mai bn stergi din front
        notifyReceiver(notificationTemplateMapper.fromModelToResponse(m), NotificationNotifyType.REMOVED);
    }


    @Override
    public List<RESPONSE> getAllBySenderEmailAndType(String senderEmail, E type) {
        Long senderId = conversationUserService.getUserByEmail(senderEmail).getId();
        return (type == null ? notificationTemplateRepository.findAllBySenderId(senderId)
                : notificationTemplateRepository.findAllBySenderIdAndType(senderId, type))
                .stream().map(notificationTemplateMapper::fromModelToResponse)
                .toList();
    }


    @Override
    public void deleteAllBySenderEmailAndType(String senderEmail, E type) {
        Long senderId = conversationUserService.getUserByEmail(senderEmail).getId();
        if (type == null) {
            notificationTemplateRepository.deleteAllBySenderId(senderId);
        } else {
            notificationTemplateRepository.deleteAllBySenderIdAndType(senderId, type);
        }
        // todo mai bn stergi din front
//        notifyReceiver(null, NotificationNotifyType.REMOVED);
    }


    public CompletableFuture<MODEL> fromBodyToModel(BODY body) {

        CompletableFuture<ConversationUser> senderFuture = conversationUserService.getUserByEmailAsync(body.getSenderEmail());
        CompletableFuture<ConversationUser> receiverFuture = conversationUserService.getUserByEmailAsync(body.getReceiverEmail());
        CompletableFuture<R> referenceFuture = referenceRepository.findById(body.getReferenceId())
                .map(CompletableFuture::completedFuture)
                .orElseThrow(() -> new EntityNotFound(referenceName, body.getReferenceId()));

        return CompletableFuture.allOf(senderFuture, receiverFuture, referenceFuture)
                .thenApplyAsync(u -> {
                    try {
                        ConversationUser sender = senderFuture.get();
                        ConversationUser receiver = receiverFuture.get();
                        R reference = referenceFuture.get();
                        return createModelInstance(sender, receiver, body.getType(), reference, body.getContent(), body.getExtraLink());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }, asyncExecutor);
    }

    public MODEL getNotificationById(Long id) {
        return notificationTemplateRepository.findById(id)
                .orElseThrow(() -> new EntityNotFound("Notification", id));
    }

    public void notifyReceiver(RESPONSE response, NotificationNotifyType notificationNotifyType) {
        String type = notificationNotifyType.name().toLowerCase();
        messagingTemplate.convertAndSendToUser(response.getReceiver().getEmail(), "/queue/notification/" + notificationName + "/" + type, response);

    }

    @Override
    public List<RESPONSE> getAllByReceiverEmailAndType(String senderEmail, E type) {
        Long receiverId = conversationUserService.getUserByEmail(senderEmail).getId();
        return (type == null ? notificationTemplateRepository.findAllByReceiverId(receiverId)
                : notificationTemplateRepository.findAllByReceiverIdAndType(receiverId, type))
                .stream().map(notificationTemplateMapper::fromModelToResponse)
                .toList();
    }


    @Override
    public void deleteAllByReceiverEmailAndType(String senderEmail, E type) {
        Long receiverId = conversationUserService.getUserByEmail(senderEmail).getId();
        if (type == null) {
            notificationTemplateRepository.deleteAllByReceiverId(receiverId);
        } else {
            notificationTemplateRepository.deleteAllByReceiverIdAndType(receiverId, type);
        }
        // todo mai bn stergi din front
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.REPEATABLE_READ)
//    @Transactional
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
    public void deleteAllByReceiverEmailSenderEmailAndType(String senderEmail, String receiverEmail, E type) {
        CompletableFuture<ConversationUser> senderFuture = conversationUserService.getUserByEmailAsync(senderEmail);
        CompletableFuture<ConversationUser> receiverFuture = conversationUserService.getUserByEmailAsync(receiverEmail);

        Map<String, ConversationUser> userMap = CompletableFuture.allOf(senderFuture, receiverFuture)
                .thenApplyAsync(v -> {
                    try {
                        Map<String, ConversationUser> users = new HashMap<>();
                        users.put("sender", senderFuture.get());
                        users.put("receiver", receiverFuture.get());
                        return users;
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }, asyncExecutor).join();


        ConversationUser sender = userMap.get("sender");
        ConversationUser receiver = userMap.get("receiver");

        if (type == null) {
            notificationTemplateRepository.deleteAllBySenderIdAndReceiverId(sender.getId(), receiver.getId());
        } else {
            notificationTemplateRepository.deleteAllBySenderIdAndReceiverIdAndType(sender.getId(), receiver.getId(), type);
        }
    }


    public CompletableFuture<Void> notifyDeleteByReferenceId(Long referenceId, List<String> receiverEmails) {
        return CompletableFuture
                .runAsync(() -> CompletableFuture.allOf(
                        receiverEmails.stream()
                                .map(email -> CompletableFuture.runAsync(() ->
                                        messagingTemplate.convertAndSendToUser(email, "/queue/notification/" + notificationName + "/removed", referenceId.toString()), asyncExecutor))
                                .toArray(CompletableFuture[]::new)
                ).join(), asyncExecutor);
    }


    protected abstract MODEL createModelInstance(ConversationUser sender, ConversationUser receiver, E type, R reference, String content, String extraLink);

}
