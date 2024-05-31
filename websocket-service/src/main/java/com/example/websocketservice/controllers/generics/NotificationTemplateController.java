package com.example.websocketservice.controllers.generics;


import com.example.websocketservice.dtos.generic.IdResponse;
import com.example.websocketservice.dtos.generic.NotificationTemplateBody;
import com.example.websocketservice.dtos.generic.NotificationTemplateResponse;
import com.example.websocketservice.dtos.notifications.SenderEmailReceiverEmailDto;
import com.example.websocketservice.dtos.notifications.SenderTypeDto;

import java.util.List;

public interface NotificationTemplateController<RRESP extends IdResponse, E extends Enum<E>,
        BODY extends NotificationTemplateBody<E>, RESPONSE extends NotificationTemplateResponse<RRESP, E>> {


    void sendNotification(BODY body);

    void deleteById(Long id);

    List<RESPONSE> getAllBySenderEmailAndType(SenderTypeDto<E> senderTypeDto);

    void deleteAllBySenderEmailAndType(SenderTypeDto<E> senderTypeDto);

    List<RESPONSE> getAllByReceiverEmailAndType(SenderTypeDto<E> senderTypeDto);

    void deleteAllByReceiverEmailAndType(SenderTypeDto<E> senderTypeDto);

//    void deleteAllBySenderIdReceiverIdAndType(Long senderId, Long receiverId, E type);

    void deleteAllByReceiverEmailSenderEmail(SenderEmailReceiverEmailDto senderEmailReceiverEmailDto);
}
