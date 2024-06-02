package com.example.websocketservice.controllers;

import com.example.websocketservice.dtos.chatRoom.ChatRoomPayload;
import com.example.websocketservice.dtos.chatRoom.ChatRoomResponse;
import com.example.websocketservice.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ChatRoomController {
    // todo sort order users and conversation like proiect sem1

    private final ChatRoomService chatRoomService;

    @MessageMapping("/addChatRoom")
    public void addChatRoom(@Payload ChatRoomPayload chatRoomPayload) {
        chatRoomService.createChatRoom(chatRoomPayload);
    }

    @GetMapping("/chatRooms/{email}")
    public ResponseEntity<List<ChatRoomResponse>> getChatRooms(@PathVariable String email) {
        return ResponseEntity.ok(chatRoomService.getChatRooms(email));
    }

}
