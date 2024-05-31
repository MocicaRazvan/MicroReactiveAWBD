package com.example.websocketservice.mappers.generic;

import com.example.websocketservice.dtos.generic.IdResponse;
import com.example.websocketservice.dtos.generic.NotificationTemplateBody;
import com.example.websocketservice.dtos.generic.NotificationTemplateResponse;
import com.example.websocketservice.models.generic.IdGenerated;
import com.example.websocketservice.models.generic.NotificationTemplate;

public abstract class NotificationTemplateMapper<R extends IdGenerated, RRESP extends IdResponse, E extends Enum<E>,
        MODEL extends NotificationTemplate<R, E>, BODY extends NotificationTemplateBody<E>, RESPONSE extends NotificationTemplateResponse<RRESP, E>> {

    public abstract RESPONSE fromModelToResponse(MODEL model);

//    public abstract MODEL fromBodyToModel(BODY body);
}
