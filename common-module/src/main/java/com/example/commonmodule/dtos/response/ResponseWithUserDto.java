package com.example.commonmodule.dtos.response;

import com.example.commonmodule.dtos.UserDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@Data
@SuperBuilder
@Schema(description = "The comment response dto")
@AllArgsConstructor
public class ResponseWithUserDto<T> {
    private T model;
    private UserDto user;
}
