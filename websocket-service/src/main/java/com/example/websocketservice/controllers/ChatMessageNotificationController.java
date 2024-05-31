package com.example.websocketservice.controllers;

import com.example.websocketservice.controllers.generics.NotificationTemplateController;
import com.example.websocketservice.dtos.chatRoom.ChatRoomResponse;
import com.example.websocketservice.dtos.notifications.ChatMessageNotificationBody;
import com.example.websocketservice.dtos.notifications.ChatMessageNotificationResponse;
import com.example.websocketservice.dtos.notifications.SenderEmailReceiverEmailDto;
import com.example.websocketservice.dtos.notifications.SenderTypeDto;
import com.example.websocketservice.enums.ChatMessageNotificationType;
import com.example.websocketservice.service.ChatMessageNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;


import java.util.List;


@RequiredArgsConstructor
@Controller
public class ChatMessageNotificationController implements
        NotificationTemplateController<ChatRoomResponse, ChatMessageNotificationType, ChatMessageNotificationBody, ChatMessageNotificationResponse> {


    private final ChatMessageNotificationService chatMessageNotificationService;

    @MessageMapping("/chatMessageNotification/sendNotification")
    @Override
    public void sendNotification(@Payload ChatMessageNotificationBody body) {
        chatMessageNotificationService.saveNotification(body);
    }

    @MessageMapping("/chatMessageNotification/deleteNotification/{id}")
    @Override
    public void deleteById(@DestinationVariable Long id) {
        chatMessageNotificationService.deleteById(id);
    }

    @GetMapping("/chatMessageNotification/getAllBySenderEmailAndType")
    @Override
    public List<ChatMessageNotificationResponse> getAllBySenderEmailAndType(@RequestBody SenderTypeDto<ChatMessageNotificationType> senderTypeDto) {
        return chatMessageNotificationService.getAllBySenderEmailAndType(senderTypeDto.getSenderEmail(), senderTypeDto.getType());
    }

    @MessageMapping("/chatMessageNotification/deleteAllBySenderEmailAndType")
    @Override
    public void deleteAllBySenderEmailAndType(@Payload SenderTypeDto<ChatMessageNotificationType> senderTypeDto) {
        chatMessageNotificationService.deleteAllBySenderEmailAndType(senderTypeDto.getSenderEmail(), senderTypeDto.getType());
    }

    @GetMapping("/chatMessageNotification/getAllByReceiverEmailAndType")
    @Override
    public List<ChatMessageNotificationResponse> getAllByReceiverEmailAndType(SenderTypeDto<ChatMessageNotificationType> senderTypeDto) {
        return chatMessageNotificationService.getAllByReceiverEmailAndType(senderTypeDto.getSenderEmail(), senderTypeDto.getType());
    }

    @MessageMapping("/chatMessageNotification/deleteAllByReceiverEmailAndType")
    @Override
    public void deleteAllByReceiverEmailAndType(SenderTypeDto<ChatMessageNotificationType> senderTypeDto) {
        chatMessageNotificationService.deleteAllByReceiverEmailAndType(senderTypeDto.getSenderEmail(), senderTypeDto.getType());
    }

    @MessageMapping("/chatMessageNotification/deleteAllByReceiverEmailSenderEmail")
    @Override
    public void deleteAllByReceiverEmailSenderEmail(SenderEmailReceiverEmailDto senderEmailReceiverEmailDto) {
        chatMessageNotificationService.deleteAllByReceiverEmailSenderEmailAndType(senderEmailReceiverEmailDto.getSenderEmail(), senderEmailReceiverEmailDto.getReceiverEmail(), null);
    }


}
