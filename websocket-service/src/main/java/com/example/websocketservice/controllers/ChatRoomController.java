package com.example.websocketservice.controllers;

import com.example.websocketservice.dtos.chatRoom.ChatRoomPayload;
import com.example.websocketservice.dtos.chatRoom.ChatRoomResponse;
import com.example.websocketservice.dtos.chatRoom.DeleteChatRoomRequest;
import com.example.websocketservice.service.ChatRoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ChatRoomController {
    // todo sort order users and conversation like project sem1

    private final ChatRoomService chatRoomService;

    @MessageMapping("/addChatRoom")
    public void addChatRoom(@Payload ChatRoomPayload chatRoomPayload) {
        chatRoomService.createChatRoom(chatRoomPayload);
    }

    @GetMapping("/chatRooms/{email}")
    public ResponseEntity<List<ChatRoomResponse>> getChatRooms(@PathVariable String email) {
        return ResponseEntity.ok(chatRoomService.getChatRooms(email));
    }

    @DeleteMapping("/chatRooms")
    public ResponseEntity<Void> deleteChatRoom(@Valid @RequestBody DeleteChatRoomRequest deleteChatRoomRequest) {
        chatRoomService.deleteChatRoom(deleteChatRoomRequest.getChatRoomId(), deleteChatRoomRequest.getSenderEmail());
        return ResponseEntity.noContent().build();
    }

}
