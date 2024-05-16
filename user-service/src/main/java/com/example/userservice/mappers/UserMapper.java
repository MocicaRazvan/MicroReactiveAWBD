package com.example.userservice.mappers;


import com.example.commonmodule.dtos.UserDto;
import com.example.userservice.dtos.auth.response.AuthResponse;
import com.example.userservice.dtos.auth.requests.RegisterRequest;
import com.example.userservice.models.UserCustom;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

@Mapper(componentModel = "spring")
public abstract class UserMapper {
    @Autowired
    protected PasswordEncoder passwordEncoder;

    @Mapping(target = "password", expression = "java(passwordEncoder.encode(registerRequest.getPassword()))")
    @Mapping(target = "role", expression = "java(com.example.commonmodule.enums.Role.ROLE_USER)")
    public abstract UserCustom fromRegisterRequestToUserCustom(RegisterRequest registerRequest);

    public abstract AuthResponse fromUserCustomToAuthResponse(UserCustom userCustom);

    public abstract UserDto fromUserCustomToUserDto(UserCustom userCustom);
}
