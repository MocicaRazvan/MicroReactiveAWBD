package com.example.orderservice.exceptions;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MessageSendingException extends RuntimeException {
    public MessageSendingException(String message, Throwable cause) {
        super(message, cause);
    }
}