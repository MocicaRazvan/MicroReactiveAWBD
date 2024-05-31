package com.example.websocketservice.repositories;

import com.example.websocketservice.dtos.chatRoom.ChatRoomUserDto;
import com.example.websocketservice.models.ChatRoom;
import com.example.websocketservice.repositories.generic.IdGeneratedRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ChatRoomRepository extends IdGeneratedRepository<ChatRoom> {
    @Query("""
                select cr from ChatRoom cr
                join cr.users u
                where u.email in :userEmail
                group by cr.id
                having count(u.id) = :count
            """)
    List<ChatRoom> findByUsers(Set<String> userEmail, long count);

    @Query("""
            select new com.example.websocketservice.dtos.chatRoom.ChatRoomUserDto(cr.id,u.email)  from ChatRoom cr
            join cr.users u
            where u.email != :senderEmail
            and cr.id in (
                select cr2.id from ChatRoom cr2
                join cr2.users u2
                where u2.email = :senderEmail
                )
            """)
    List<ChatRoomUserDto> findOthersEmailsBySenderEmail(String senderEmail);


    @Query("""
            select cr from ChatRoom cr
            join cr.users u
            where u.email = :userEmail
            """)
    List<ChatRoom> findChatRoomsByUserEmail(String userEmail);
}
