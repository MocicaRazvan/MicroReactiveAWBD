package com.example.commentservice.hateos;


import com.example.commentservice.controllers.CommentController;
import com.example.commentservice.dtos.CommentBody;
import com.example.commentservice.dtos.CommentResponse;
import com.example.commentservice.mappers.CommentMapper;
import com.example.commentservice.models.Comment;
import com.example.commentservice.repositories.CommentRepository;
import com.example.commentservice.services.CommentService;
import com.example.commonmodule.hateos.controllerMaybe.generics.TitleBodyReactiveLinkBuilder;
import org.springframework.hateoas.server.reactive.WebFluxLinkBuilder;

import java.util.List;


public class CommentReactiveLinkBuilder extends TitleBodyReactiveLinkBuilder<Comment, CommentBody, CommentResponse,
        CommentRepository, CommentMapper, CommentService, CommentController> {

    @Override
    public List<WebFluxLinkBuilder.WebFluxLink> createModelLinks(CommentResponse commentResponse, Class<CommentController> c) {
        List<WebFluxLinkBuilder.WebFluxLink> links = super.createModelLinks(commentResponse, c);
        links.add(WebFluxLinkBuilder.linkTo(WebFluxLinkBuilder.methodOn(c).createComment(commentResponse.getPostId(), null, null)).withRel("create"));
        links.add(WebFluxLinkBuilder.linkTo(WebFluxLinkBuilder.methodOn(c).getCommentsByPost(commentResponse.getPostId(), null)).withRel("getByPost"));
        links.add(WebFluxLinkBuilder.linkTo(WebFluxLinkBuilder.methodOn(c).getCommentsByUser(commentResponse.getUserId(), null)).withRel("getByUser"));
        return links;
    }
}
