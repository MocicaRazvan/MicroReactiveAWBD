package com.example.websocketservice.controllers;


import com.example.websocketservice.dtos.message.ChatMessagePayload;
import com.example.websocketservice.dtos.message.ChatMessageResponse;
import com.example.websocketservice.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ChatMessageController {

    private final ChatMessageService chatMessageService;

    @MessageMapping("/sendMessage")
    public void sendMessage(@Payload ChatMessagePayload chatMessagePayload) {
        chatMessageService.sendMessage(chatMessagePayload);
    }

    @GetMapping("/messages/{chatRoomId}")
    public ResponseEntity<List<ChatMessageResponse>> getMessages(@PathVariable Long chatRoomId) {
        return ResponseEntity.ok(chatMessageService.getMessages(chatRoomId));
    }

}
