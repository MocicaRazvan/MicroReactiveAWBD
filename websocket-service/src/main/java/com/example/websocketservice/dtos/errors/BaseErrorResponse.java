package com.example.websocketservice.dtos.errors;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class BaseErrorResponse {
    private String message;
    private String timestamp;
    private String error;
    private String path;
    private int status;
}
