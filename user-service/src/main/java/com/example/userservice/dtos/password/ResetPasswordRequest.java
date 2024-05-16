package com.example.userservice.dtos.password;


import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ResetPasswordRequest extends ForgotPasswordRequest {
    @NotEmpty(message = "Token should be not empty!")
    private String token;
    @NotEmpty(message = "New password should be not empty!")
    private String newPassword;
}
