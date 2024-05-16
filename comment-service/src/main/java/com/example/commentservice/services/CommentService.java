package com.example.commentservice.services;


import com.example.commentservice.dtos.CommentBody;
import com.example.commentservice.dtos.CommentResponse;
import com.example.commentservice.mappers.CommentMapper;
import com.example.commentservice.models.Comment;
import com.example.commentservice.repositories.CommentRepository;
import com.example.commonmodule.dtos.PageableBody;
import com.example.commonmodule.dtos.response.PageableResponse;
import com.example.commonmodule.dtos.response.ResponseWithUserDto;
import com.example.commonmodule.enums.Role;
import com.example.commonmodule.services.TitleBodyService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CommentService extends TitleBodyService<Comment, CommentBody, CommentResponse, CommentRepository, CommentMapper> {
    Mono<CommentResponse> createModel(Long postId, CommentBody commentBody, String userId);

    Flux<PageableResponse<CommentResponse>> getCommentsByPost(Long postId, PageableBody pageableBody);

    Flux<PageableResponse<ResponseWithUserDto<CommentResponse>>> getCommentsWithUserByPost(Long postId, PageableBody pageableBody);

    Flux<ResponseWithUserDto<CommentResponse>> getCommentsByPost(Long postId);

    Flux<PageableResponse<CommentResponse>> getModelByUser(Long userId, PageableBody pageableBody);

    Mono<Void> deleteCommentsByPost(Long postId, String userId, Role role);

}
