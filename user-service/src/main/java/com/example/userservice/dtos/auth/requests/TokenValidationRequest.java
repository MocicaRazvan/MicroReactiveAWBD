package com.example.userservice.dtos.auth.requests;

import com.example.commonmodule.enums.Role;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TokenValidationRequest {

    @NotNull
    private String token;

    @NotNull
    private Role minRoleRequired;
}
