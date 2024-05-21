package com.example.invoicesservice.models;

import com.example.invoicesservice.enums.AuthProvider;
import com.example.invoicesservice.enums.Role;
import com.example.invoicesservice.models.generics.IdGenerated;
import jakarta.persistence.*;
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
@Entity
@Table(name = "user_custom")
public class UserCustom extends IdGenerated {

    private String firstName;
    private String lastName;
    @Column(nullable = false, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    private Role role = Role.ROLE_USER;

    @Enumerated(EnumType.STRING)
    private AuthProvider provider = AuthProvider.LOCAL;

    @Column(name = "is_email_verified")
    private boolean emailVerified;
}
