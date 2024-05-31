package com.example.websocketservice.repositories;

import com.example.websocketservice.enums.ConnectedStatus;
import com.example.websocketservice.models.ConversationUser;
import com.example.websocketservice.repositories.generic.IdGeneratedRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ConversationUserRepository extends IdGeneratedRepository<ConversationUser> {
    Optional<ConversationUser> findByEmail(String email);

    boolean existsByEmail(String email);

    List<ConversationUser> findAllByConnectedStatusIs(ConnectedStatus connectedStatus);
}
