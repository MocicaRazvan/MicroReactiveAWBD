package com.example.commonmodule.dtos;

import com.example.commonmodule.dtos.generic.IdDto;
import com.example.commonmodule.enums.AuthProvider;
import com.example.commonmodule.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Schema(description = "The user dto")
public class UserDto extends IdDto {


    @Schema(description = "The user's first name")
    private String firstName;

    @Schema(description = "The user's last name")
    private String lastName;

    @Schema(description = "The user's email")
    private String email;

    @Schema(description = "The user's role", defaultValue = "ROLE_USER")
    private Role role = Role.ROLE_USER;

    private AuthProvider provider;

    private String image;
}