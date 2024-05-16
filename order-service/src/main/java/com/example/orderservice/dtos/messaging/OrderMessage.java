package com.example.orderservice.dtos.messaging;

import com.example.commonmodule.utils.Transformable;
import com.example.orderservice.dtos.OrderResponse;
import com.example.orderservice.enums.MessageType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
@SuperBuilder
public class OrderMessage extends OrderResponse implements Transformable<OrderMessage> {
    private MessageType messageType;
}
