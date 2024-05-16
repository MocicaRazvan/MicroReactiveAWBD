package com.example.postservice.hateos;


import com.example.commonmodule.hateos.controllerMaybe.ReactiveResponseBuilder;
import com.example.commonmodule.hateos.user.UserDtoAssembler;
import com.example.postservice.controllers.PostController;
import com.example.postservice.dtos.PostResponse;
import org.springframework.stereotype.Component;


@Component
public class PostReactiveResponseBuilder extends ReactiveResponseBuilder<PostResponse, PostController> {
    public PostReactiveResponseBuilder() {
        super(new PostReactiveLinkBuilder());
    }
}
