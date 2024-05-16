package com.example.invoicesservice.dtos;


import com.example.invoicesservice.dtos.generic.IdDto;
import com.example.invoicesservice.enums.AuthProvider;
import com.example.invoicesservice.enums.Role;
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
public class UserDto extends IdDto {


    private String firstName;

    private String lastName;

    private String email;

    private Role role = Role.ROLE_USER;

    private String image;

    private AuthProvider provider;
}