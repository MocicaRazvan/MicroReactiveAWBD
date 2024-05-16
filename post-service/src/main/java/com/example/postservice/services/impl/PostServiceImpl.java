package com.example.postservice.services.impl;


import com.example.commonmodule.dtos.response.ResponseWithChildList;
import com.example.commonmodule.dtos.response.ResponseWithUserDto;
import com.example.commonmodule.exceptions.notFound.NotFoundEntity;
import com.example.commonmodule.services.impl.ApprovedServiceImpl;
import com.example.commonmodule.utils.EntitiesUtils;
import com.example.commonmodule.utils.PageableUtilsCustom;
import com.example.commonmodule.utils.UserUtils;
import com.example.postservice.clients.CommentClient;
import com.example.postservice.dtos.PostBody;
import com.example.postservice.dtos.PostResponse;
import com.example.postservice.dtos.comments.CommentResponse;
import com.example.postservice.mappers.PostMapper;
import com.example.postservice.models.Post;
import com.example.postservice.repositories.PostRepository;
import com.example.postservice.services.PostService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;


@Service

public class PostServiceImpl extends ApprovedServiceImpl<Post, PostBody, PostResponse, PostRepository, PostMapper>
        implements PostService {

    private final CommentClient commentClient;

    public PostServiceImpl(PostRepository modelRepository, PostMapper modelMapper, PageableUtilsCustom pageableUtils, UserUtils userUtils, EntitiesUtils entitiesUtils, CommentClient commentClient) {
        super(modelRepository, modelMapper, pageableUtils, userUtils, "post", List.of("id", "userId", "postId", "title", "createdAt"), entitiesUtils);
        this.commentClient = commentClient;
    }


    @Override
    public Mono<PostResponse> deleteModel(Long id, String userId) {
        return userUtils.getUser("", userId)
                .flatMap(authUser -> getModel(id)
                        .flatMap(model -> privateRoute(true, authUser, model.getUserId())
                                .then(commentClient.deleteCommentsByPostId(id.toString(), userId))
                                .then(modelRepository.delete(model))
                                .then(Mono.fromCallable(() -> modelMapper.fromModelToResponse(model)))
                        )
                );
    }

    @Override
    public Mono<Void> existsByIdAndApprovedIsTrue(Long id) {
        return modelRepository.existsByIdAndApprovedIsTrue(id)
                .log()
                .filter(Boolean::booleanValue)
                .switchIfEmpty(Mono.error(new NotFoundEntity("post", id)))
                .then();

    }

    @Override
    public Mono<ResponseWithChildList<PostResponse, ResponseWithUserDto<CommentResponse>>> getPostWithComments(Long id, boolean approved) {
        return modelRepository.findByApprovedAndId(approved, id)
                .switchIfEmpty(Mono.error(new NotFoundEntity("post", id)))
                .flatMap(post -> commentClient.getCommentsByPost(post.getId().toString())
                        .collectList()
                        .map(comments -> new ResponseWithChildList<>(modelMapper.fromModelToResponse(post), comments))
                );
    }


}
