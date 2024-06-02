package com.example.websocketservice.controllers;

import com.example.websocketservice.controllers.generics.NotificationTemplateController;
import com.example.websocketservice.dtos.chatRoom.ChatRoomResponse;
import com.example.websocketservice.dtos.notifications.ChatMessageNotificationBody;
import com.example.websocketservice.dtos.notifications.ChatMessageNotificationResponse;
import com.example.websocketservice.dtos.notifications.SenderEmailReceiverEmailDto;
import com.example.websocketservice.dtos.notifications.SenderTypeDto;
import com.example.websocketservice.enums.ChatMessageNotificationType;
import com.example.websocketservice.service.ChatMessageNotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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

    @PatchMapping("/chatMessageNotification/getAllBySenderEmailAndType")
    @Override
    public ResponseEntity<List<ChatMessageNotificationResponse>> getAllBySenderEmailAndType(@Valid @RequestBody SenderTypeDto<ChatMessageNotificationType> senderTypeDto) {
        return ResponseEntity.ok(chatMessageNotificationService.getAllBySenderEmailAndType(senderTypeDto.getSenderEmail(), senderTypeDto.getType()));
    }

    @MessageMapping("/chatMessageNotification/deleteAllBySenderEmailAndType")
    @Override
    public void deleteAllBySenderEmailAndType(@Payload SenderTypeDto<ChatMessageNotificationType> senderTypeDto) {
        chatMessageNotificationService.deleteAllBySenderEmailAndType(senderTypeDto.getSenderEmail(), senderTypeDto.getType());
    }

    @PatchMapping("/chatMessageNotification/getAllByReceiverEmailAndType")
    @Override
    public ResponseEntity<List<ChatMessageNotificationResponse>> getAllByReceiverEmailAndType(@Valid @RequestBody SenderTypeDto<ChatMessageNotificationType> senderTypeDto) {
        return ResponseEntity.ok(chatMessageNotificationService.getAllByReceiverEmailAndType(senderTypeDto.getSenderEmail(), senderTypeDto.getType()));
    }

    @MessageMapping("/chatMessageNotification/deleteAllByReceiverEmailAndType")
    @Override
    public void deleteAllByReceiverEmailAndType(@Payload SenderTypeDto<ChatMessageNotificationType> senderTypeDto) {
        chatMessageNotificationService.deleteAllByReceiverEmailAndType(senderTypeDto.getSenderEmail(), senderTypeDto.getType());
    }

    @MessageMapping("/chatMessageNotification/deleteAllByReceiverEmailSenderEmail")
    @Override
    public void deleteAllByReceiverEmailSenderEmail(@Payload SenderEmailReceiverEmailDto senderEmailReceiverEmailDto) {
        chatMessageNotificationService.deleteAllByReceiverEmailSenderEmailAndType(senderEmailReceiverEmailDto.getSenderEmail(), senderEmailReceiverEmailDto.getReceiverEmail(), null);
    }


}
