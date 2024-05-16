package com.example.postservice.services;

import com.example.commonmodule.dtos.response.ResponseWithChildList;
import com.example.commonmodule.dtos.response.ResponseWithUserDto;
import com.example.commonmodule.services.ApprovedService;
import com.example.postservice.dtos.PostBody;
import com.example.postservice.dtos.PostResponse;
import com.example.postservice.dtos.comments.CommentResponse;
import com.example.postservice.mappers.PostMapper;
import com.example.postservice.models.Post;
import com.example.postservice.repositories.PostRepository;
import reactor.core.publisher.Mono;


public interface PostService extends ApprovedService<Post, PostBody, PostResponse, PostRepository, PostMapper> {

    Mono<Void> existsByIdAndApprovedIsTrue(Long id);

    // todo post with comments
    Mono<ResponseWithChildList<PostResponse, ResponseWithUserDto<CommentResponse>>>
    getPostWithComments(Long id, boolean approved);
}
