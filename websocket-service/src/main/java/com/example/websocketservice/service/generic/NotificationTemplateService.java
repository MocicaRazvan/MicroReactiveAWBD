package com.example.websocketservice.service.generic;

import com.example.websocketservice.dtos.generic.IdResponse;
import com.example.websocketservice.dtos.generic.NotificationTemplateBody;
import com.example.websocketservice.dtos.generic.NotificationTemplateResponse;
import com.example.websocketservice.models.generic.IdGenerated;
import com.example.websocketservice.models.generic.NotificationTemplate;
import com.example.websocketservice.repositories.generic.IdGeneratedRepository;
import com.example.websocketservice.repositories.generic.NotificationTemplateRepository;

public interface NotificationTemplateService<R extends IdGenerated, RRESP extends IdResponse, E extends Enum<E>,
        MODEL extends NotificationTemplate<R, E>, BODY extends NotificationTemplateBody<E>, RESPONSE extends NotificationTemplateResponse<RRESP, E>> {

    RESPONSE saveNotification(BODY body);

    void deleteNotification(Long id);

}
