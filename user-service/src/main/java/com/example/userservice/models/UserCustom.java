package com.example.userservice.models;

import com.example.commonmodule.enums.AuthProvider;
import com.example.commonmodule.enums.Role;
import com.example.commonmodule.models.IdGenerated;
import com.example.userservice.authorities.GrantedAuthority;
import com.example.userservice.authorities.SimpleGrantedAuthority;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;


import java.util.Collection;
import java.util.List;


@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Table(name = "user_custom")
public class UserCustom extends IdGenerated {
    @Column("first_name")
    private String firstName;
    @Column("last_name")
    private String lastName;
    @Column("email")
    private String email;
    @Column("password")
    private String password;
    @Column("role")
    private Role role = Role.ROLE_USER;
    @Column("image")
    private String image;

    @Column("provider")
    private AuthProvider provider;

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    public String getUsername() {
        return email;
    }

}
