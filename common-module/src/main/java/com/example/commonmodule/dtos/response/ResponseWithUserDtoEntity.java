package com.example.commonmodule.dtos.response;

import com.example.commonmodule.dtos.UserDto;
import com.example.commonmodule.hateos.CustomEntityModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseWithUserDtoEntity<T> {
    private CustomEntityModel<T> model;
    private UserDto user;
}
