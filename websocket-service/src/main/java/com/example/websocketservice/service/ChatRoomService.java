package com.example.websocketservice.service;

import com.example.websocketservice.dtos.chatRoom.ChatRoomPayload;
import com.example.websocketservice.dtos.chatRoom.ChatRoomResponse;
import com.example.websocketservice.dtos.chatRoom.ChatRoomUserDto;
import com.example.websocketservice.models.ChatRoom;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

public interface ChatRoomService {

    ChatRoomResponse createChatRoom(ChatRoomPayload chatRoomPayload);


    List<ChatRoom> getRoomsByUsers(Set<String> emails);


    List<ChatRoomResponse> getChatRooms(String email);

    void deleteChatRoom(Long id, String senderEmail);
}
