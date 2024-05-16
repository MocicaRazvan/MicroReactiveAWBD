package com.example.userservice.dtos.password;


import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ForgotPasswordRequest {

    @NotEmpty(message = "Email should be not empty!")
    private String email;
}
