package com.example.websocketservice.repositories;

import com.example.websocketservice.enums.ConnectedStatus;
import com.example.websocketservice.models.ConversationUser;
import com.example.websocketservice.repositories.generic.IdGeneratedRepository;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.List;
import java.util.Optional;

public interface ConversationUserRepository extends IdGeneratedRepository<ConversationUser> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<ConversationUser> findByEmail(String email);

    boolean existsByEmail(String email);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<ConversationUser> findAllByConnectedStatusIs(ConnectedStatus connectedStatus);
}
