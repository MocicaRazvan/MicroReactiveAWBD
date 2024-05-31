package com.example.websocketservice.dtos.notifications;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class SenderTypeDto<E extends Enum<E>> {
    private String senderEmail;
    private E type;
}
