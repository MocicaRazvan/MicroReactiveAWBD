package com.example.postservice.hateos;

import com.example.commonmodule.hateos.controllerMaybe.generics.ApproveReactiveLinkBuilder;
import com.example.postservice.controllers.PostController;
import com.example.postservice.dtos.PostBody;
import com.example.postservice.dtos.PostResponse;
import com.example.postservice.mappers.PostMapper;
import com.example.postservice.models.Post;
import com.example.postservice.repositories.PostRepository;
import com.example.postservice.services.PostService;

import org.springframework.hateoas.server.reactive.WebFluxLinkBuilder;

import java.util.List;

public class PostReactiveLinkBuilder extends ApproveReactiveLinkBuilder<Post, PostBody, PostResponse, PostRepository, PostMapper, PostService, PostController> {


    @Override
    public List<WebFluxLinkBuilder.WebFluxLink> createModelLinks(PostResponse postResponse, Class<PostController> c) {
        List<WebFluxLinkBuilder.WebFluxLink> links = super.createModelLinks(postResponse, c);
//       todo
//        links.add(WebFluxLinkBuilder.linkTo(WebFluxLinkBuilder.methodOn(c).getPostWithComments(postResponse.getId())).withRel("getWithComments"));
        return links;
    }
}
