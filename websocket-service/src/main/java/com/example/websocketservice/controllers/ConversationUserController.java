package com.example.websocketservice.controllers;


import com.example.websocketservice.dtos.chatRoom.ChatRoomUserDto;
import com.example.websocketservice.dtos.user.ConversationUserPayload;
import com.example.websocketservice.dtos.user.ConversationUserResponse;
import com.example.websocketservice.enums.ConnectedStatus;
import com.example.websocketservice.service.ConversationUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ConversationUserController {
    // todo sort order users and conversation like proiect sem1
    private final ConversationUserService conversationUserService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/addUser")
    public void addUser(@Payload ConversationUserPayload conversationUserPayload) {
        conversationUserService.addUser(conversationUserPayload)
                .map(cur ->
                {
                    simpMessagingTemplate.convertAndSend("/chat/connected",
                            cur);
                    return null;
                });

    }

    @MessageMapping("/connectUser/{email}")
    public void connectUser(@DestinationVariable String email) {
        conversationUserService.changeUserConnectedStatus(ConnectedStatus.ONLINE, email)
                .map(cur -> {
                    simpMessagingTemplate.convertAndSend("/chat/connected",
                            cur);
                    return null;
                });
    }

    @MessageMapping("/disconnectUser/{email}")
    public void disconnectUser(@DestinationVariable String email) {
        log.error("Disconnect user: {}", email);
        conversationUserService.changeUserConnectedStatus(ConnectedStatus.OFFLINE, email)
                .map(cur -> {
                    simpMessagingTemplate.convertAndSend("/chat/connected",
                            cur);
                    return null;
                });
    }

    @GetMapping("/getConnectedUsers")
    public ResponseEntity<List<ConversationUserResponse>> getConnectedUsers() {
        return ResponseEntity.ok(conversationUserService.getConnectedUsers());
    }

    @MessageMapping("/changeRoom")
    public void changeRoom(@Payload ChatRoomUserDto chatRoomUserDto) {
        log.error("Change room: {}", chatRoomUserDto);
        conversationUserService.changeUserChatRoom(chatRoomUserDto)
                .map(cur -> {
                    simpMessagingTemplate.convertAndSend("/chat/connected",
                            cur);
                    return null;
                });
    }


}
