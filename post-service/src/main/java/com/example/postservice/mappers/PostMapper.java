package com.example.postservice.mappers;


import com.example.commonmodule.mappers.DtoMapper;
import com.example.postservice.dtos.PostBody;
import com.example.postservice.dtos.PostResponse;
import com.example.postservice.models.Post;
import org.mapstruct.Mapper;
import reactor.core.publisher.Mono;


@Mapper(componentModel = "spring")
public abstract class PostMapper extends DtoMapper<Post, PostBody, PostResponse> {

    @Override
    public Mono<Post> updateModelFromBody(PostBody body, Post post) {
        post.setTags(body.getTags());
        post.setTitle(body.getTitle());
        post.setBody(body.getBody());
        post.setApproved(false);
        post.setImages(body.getImages());
        return Mono.just(post);
    }
}
