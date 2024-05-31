package com.example.websocketservice.dtos.user;

import com.example.websocketservice.dtos.generic.IdResponse;
import com.example.websocketservice.enums.ConnectedStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
// todo verify
public class ConversationUserBase extends IdResponse {
    private String email;
    private ConnectedStatus connectedStatus;
}
