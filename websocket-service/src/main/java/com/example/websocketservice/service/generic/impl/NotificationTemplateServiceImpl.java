package com.example.websocketservice.service.generic.impl;

import com.example.websocketservice.dtos.generic.IdResponse;
import com.example.websocketservice.dtos.generic.NotificationTemplateBody;
import com.example.websocketservice.dtos.generic.NotificationTemplateResponse;
import com.example.websocketservice.enums.NotificationNotifyType;
import com.example.websocketservice.exceptions.EntityNotFound;
import com.example.websocketservice.mappers.generic.NotificationTemplateMapper;
import com.example.websocketservice.models.ConversationUser;
import com.example.websocketservice.models.generic.IdGenerated;
import com.example.websocketservice.models.generic.NotificationTemplate;
import com.example.websocketservice.repositories.generic.IdGeneratedRepository;
import com.example.websocketservice.repositories.generic.NotificationTemplateRepository;
import com.example.websocketservice.service.ConversationUserService;
import com.example.websocketservice.service.generic.NotificationTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@RequiredArgsConstructor
public class NotificationTemplateServiceImpl<R extends IdGenerated, RRESP extends IdResponse, E extends Enum<E>,
        MODEL extends NotificationTemplate<R, E>, BODY extends NotificationTemplateBody<E>, RESPONSE extends NotificationTemplateResponse<RRESP, E>,
        RREPO extends IdGeneratedRepository<R>,
        MREOP extends NotificationTemplateRepository<R, E, MODEL>,
        MMAP extends NotificationTemplateMapper<R, RRESP, E, MODEL, BODY, RESPONSE>> implements NotificationTemplateService<R, RRESP, E, MODEL, BODY, RESPONSE> {

    private final RREPO referenceRepository;
    private final ConversationUserService conversationUserService;
    private final String referenceName;
    private final Executor asyncExecutor;
    private final MREOP notificationTemplateRepository;
    private final MMAP notificationTemplateMapper;
    private final SimpMessagingTemplate messagingTemplate;
    // todo get by sender, get by sender and type,  delete by sender, delete by sender and type

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
    public void deleteNotification(Long id) {
        MODEL m = getNotificationById(id);
        notificationTemplateRepository.delete(m);
        // todo mai bn stergi din front
        notifyReceiver(notificationTemplateMapper.fromModelToResponse(m), NotificationNotifyType.REMOVED);
    }

    @SuppressWarnings("unchecked")
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
                        return (MODEL) MODEL.<R, E>builder()
                                .sender(sender)
                                .receiver(receiver)
                                .type(body.getType())
                                .reference(reference)
                                .content(body.getContent())
                                .extraLink(body.getExtraLink())
                                .build();
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
        messagingTemplate.convertAndSendToUser(response.getReceiver().getEmail(), "/queue/notification/" + type, response);

    }
}
