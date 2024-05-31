package com.example.websocketservice.service.generic;

import com.example.websocketservice.dtos.generic.IdResponse;
import com.example.websocketservice.dtos.generic.NotificationTemplateBody;
import com.example.websocketservice.dtos.generic.NotificationTemplateResponse;
import com.example.websocketservice.dtos.notifications.ChatMessageNotificationResponse;
import com.example.websocketservice.enums.ChatMessageNotificationType;

import java.util.List;

public interface NotificationTemplateService<RRESP extends IdResponse, E extends Enum<E>,
        BODY extends NotificationTemplateBody<E>, RESPONSE extends NotificationTemplateResponse<RRESP, E>> {

    RESPONSE saveNotification(BODY body);

    void deleteById(Long id);

    List<RESPONSE> getAllBySenderEmailAndType(String senderEmail, E type);

    void deleteAllBySenderEmailAndType(String senderEmail, E type);

    List<RESPONSE> getAllByReceiverEmailAndType(String senderEmail, E type);

    void deleteAllByReceiverEmailAndType(String senderEmail, E type);

    void deleteAllByReceiverEmailSenderEmailAndType(String senderEmail, String receiverEmail, E type);
}
