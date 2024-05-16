package com.example.commentservice.hateos;


import com.example.commentservice.controllers.CommentController;
import com.example.commentservice.dtos.CommentResponse;
import com.example.commonmodule.hateos.controllerMaybe.ReactiveResponseBuilder;
import org.springframework.stereotype.Component;

@Component
public class CommentReactiveResponseBuilder extends ReactiveResponseBuilder<CommentResponse, CommentController> {
    public CommentReactiveResponseBuilder() {
        super(new CommentReactiveLinkBuilder());
    }
}
