package com.example.gatewayservice.dtos;

import com.example.gatewayservice.enums.Role;
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
