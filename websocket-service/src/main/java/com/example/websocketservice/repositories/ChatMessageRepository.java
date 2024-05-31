package com.example.websocketservice.repositories;

import com.example.websocketservice.models.ChatMessage;
import com.example.websocketservice.models.ChatRoom;
import com.example.websocketservice.repositories.generic.IdGeneratedRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChatMessageRepository extends IdGeneratedRepository<ChatMessage> {
    @Query("""
                select cm from ChatMessage cm
                where cm.chatRoom.id = :chatRoomId
            """)
    List<ChatMessage> findAllByChatRoomId(Long chatRoomId);
}
