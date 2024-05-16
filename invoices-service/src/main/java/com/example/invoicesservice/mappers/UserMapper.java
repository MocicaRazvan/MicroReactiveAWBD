package com.example.invoicesservice.mappers;

import com.example.invoicesservice.dtos.UserDto;
import com.example.invoicesservice.models.UserCustom;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface UserMapper extends GenericMapper<UserCustom, UserDto> {

}
