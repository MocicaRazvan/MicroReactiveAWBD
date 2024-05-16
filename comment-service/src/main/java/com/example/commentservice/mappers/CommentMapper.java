package com.example.commentservice.mappers;


import com.example.commentservice.dtos.CommentBody;
import com.example.commentservice.dtos.CommentResponse;
import com.example.commentservice.models.Comment;
import com.example.commonmodule.mappers.DtoMapper;
import org.mapstruct.Mapper;
import reactor.core.publisher.Mono;

@Mapper(componentModel = "spring")
public abstract class CommentMapper extends DtoMapper<Comment, CommentBody, CommentResponse> {


    @Override
    public Mono<Comment> updateModelFromBody(CommentBody body, Comment comment) {
        comment.setBody(body.getBody());
        comment.setTitle(body.getTitle());
        comment.setImages(body.getImages());
        return Mono.just(comment);
    }
}
