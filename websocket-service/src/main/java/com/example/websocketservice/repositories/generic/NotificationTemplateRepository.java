package com.example.websocketservice.repositories.generic;


import com.example.websocketservice.models.generic.IdGenerated;
import com.example.websocketservice.models.generic.NotificationTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

@NoRepositoryBean
public interface NotificationTemplateRepository<R extends IdGenerated, E extends Enum<E>,
        M extends NotificationTemplate<R, E>> extends JpaRepository<M, Long> {

    List<M> findAllBySenderIdAndType(Long senderId, E type);

    List<M> findAllBySenderId(Long senderId);

    List<M> findAllByReceiverId(Long receiverId);

    List<M> findAllBySenderIdAndReceiverId(Long senderId, Long receiverId);
}
