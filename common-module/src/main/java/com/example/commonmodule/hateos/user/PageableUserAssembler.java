package com.example.commonmodule.hateos.user;


import com.example.commonmodule.controllers.UserController;
import com.example.commonmodule.dtos.UserDto;
import org.springframework.stereotype.Component;

public class PageableUserAssembler extends PageableResponseAssembler<UserDto, UserDtoAssembler> {
    public PageableUserAssembler(UserDtoAssembler itemAssembler, Class<? extends UserController> userController) {
        super(itemAssembler, userController);
    }
}
