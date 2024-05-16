package com.example.commentservice.services.impl;

import com.example.commentservice.clients.PostClient;
import com.example.commentservice.dtos.CommentBody;
import com.example.commentservice.dtos.CommentResponse;
import com.example.commentservice.mappers.CommentMapper;
import com.example.commentservice.models.Comment;
import com.example.commentservice.repositories.CommentRepository;
import com.example.commentservice.services.CommentService;
import com.example.commonmodule.dtos.PageableBody;
import com.example.commonmodule.dtos.response.PageableResponse;
import com.example.commonmodule.dtos.response.ResponseWithUserDto;
import com.example.commonmodule.enums.Role;
import com.example.commonmodule.exceptions.action.PrivateRouteException;
import com.example.commonmodule.services.impl.TitleBodyServiceImpl;
import com.example.commonmodule.utils.EntitiesUtils;
import com.example.commonmodule.utils.PageableUtilsCustom;
import com.example.commonmodule.utils.UserUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;


@Service
@Slf4j
public class CommentServiceImpl extends TitleBodyServiceImpl<Comment, CommentBody, CommentResponse, CommentRepository, CommentMapper>
        implements CommentService {

    private final PostClient postClient;

    public CommentServiceImpl(CommentRepository modelRepository, CommentMapper modelMapper, PageableUtilsCustom pageableUtils, UserUtils userUtils, EntitiesUtils entitiesUtils, PostClient postClient) {
        super(modelRepository, modelMapper, pageableUtils, userUtils, "comment", List.of("id", "userId", "postId", "title", "createdAt"), entitiesUtils);
        this.postClient = postClient;
    }


    @Override
    public Mono<CommentResponse> createModel(Long postId, CommentBody commentBody, String userId) {
        return postClient.existsApprovedPost(postId.toString())
                .then(Mono.defer(() -> {
                    Comment comment = modelMapper.fromBodyToModel(commentBody);
                    comment.setPostId(postId);
                    comment.setUserId(Long.valueOf(userId));
                    comment.setUserDislikes(new ArrayList<>());
                    comment.setUserLikes(new ArrayList<>());
                    return modelRepository.save(comment)
                            .map(modelMapper::fromModelToResponse);
                }));
    }

    @Override
    public Flux<PageableResponse<CommentResponse>> getCommentsByPost(Long postId, PageableBody pageableBody) {
        return postClient.existsApprovedPost(postId.toString())
                .thenMany(pageableUtils.createPageRequest(pageableBody)
                        .flatMapMany(pr ->
                                pageableUtils.createPageableResponse(
                                        modelRepository.findAllByPostId(postId, pr).map(modelMapper::fromModelToResponse),
                                        modelRepository.countAllByPostId(postId),
                                        pr
                                )));

    }

    @Override
    public Flux<PageableResponse<ResponseWithUserDto<CommentResponse>>> getCommentsWithUserByPost(Long postId, PageableBody pageableBody) {
        return postClient.existsApprovedPost(postId.toString())
                .thenMany(pageableUtils.createPageRequest(pageableBody)
                        .flatMapMany(pr ->
                                pageableUtils.createPageableResponse(
                                        modelRepository.findAllByPostId(postId, pr).flatMap(c ->
                                                userUtils.getUser("", c.getUserId().toString())
                                                        .map(user -> ResponseWithUserDto.<CommentResponse>builder()
                                                                .user(user)
                                                                .model(modelMapper.fromModelToResponse(c))
                                                                .build()
                                                        )
                                        ),
                                        modelRepository.countAllByPostId(postId),
                                        pr
                                ))
                );
    }

    @Override
    public Flux<ResponseWithUserDto<CommentResponse>> getCommentsByPost(Long postId) {
        return postClient.existsApprovedPost(postId.toString())
                .thenMany(modelRepository.findAllByPostId(postId).flatMap(c ->
                        userUtils.getUser("", c.getUserId().toString())
                                .map(user -> ResponseWithUserDto.<CommentResponse>builder()
                                        .user(user)
                                        .model(modelMapper.fromModelToResponse(c))
                                        .build()
                                ))
                );
    }

    @Override
    public Flux<PageableResponse<CommentResponse>> getModelByUser(Long userId, PageableBody pageableBody) {
        return pageableUtils.isSortingCriteriaValid(pageableBody.getSortingCriteria(), allowedSortingFields)
                .then(userUtils.getUser("", userId.toString()))
                .thenMany(pageableUtils.createPageRequest(pageableBody)
                        .flatMapMany(pr -> pageableUtils.createPageableResponse(
                                modelRepository.findAllByUserId(userId, pr).map(modelMapper::fromModelToResponse),
                                modelRepository.countAllByUserId(userId),
                                pr
                        )));
    }

    @Override
    public Mono<Void> deleteCommentsByPost(Long postId, String userId, Role role) {
        if (role.equals(Role.ROLE_USER)) {
            return Mono.error(new IllegalArgumentException("User can't delete comments"));
        }

        return userUtils.getUser("", userId)
                .flatMap(user -> {
                    log.info("User role: {}", user.getRole());
                    if (role.equals(Role.ROLE_ADMIN) && !user.getRole().equals(Role.ROLE_ADMIN)) {
                        return Mono.error(new PrivateRouteException());
                    }
                    return postClient.getPostById(postId.toString(), userId)
                            .flatMap(post -> {
                                if (role.equals(Role.ROLE_TRAINER) && (
                                        !user.getRole().equals(Role.ROLE_ADMIN) && !post.getUserId().toString().equals(userId)
                                )) {
                                    return Mono.error(new PrivateRouteException());
                                }
                                return modelRepository.deleteAllByPostIdEquals(postId);
                            });
                })
                .then();
    }

    @Override
    public Mono<CommentResponse> deleteModel(Long id, String userId) {
        return userUtils.getUser("", userId)
                .flatMap(authUser -> getModel(id)
                        .flatMap(model -> isNotAuthor(model, authUser)
                                .map(notAuthor -> {
                                    if (notAuthor && authUser.getRole() == Role.ROLE_ADMIN) {
                                        return Mono.error(new PrivateRouteException());
                                    }
                                    return Mono.empty();
                                })
                                .then(modelRepository.delete(model))
                                .thenReturn(modelMapper.fromModelToResponse(model))
                        )
                );
    }
}
