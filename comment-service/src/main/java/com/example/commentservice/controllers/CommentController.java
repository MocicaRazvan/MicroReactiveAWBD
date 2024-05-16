package com.example.commentservice.controllers;

import com.example.commentservice.dtos.CommentBody;
import com.example.commentservice.dtos.CommentResponse;
import com.example.commentservice.hateos.CommentReactiveResponseBuilder;
import com.example.commentservice.mappers.CommentMapper;
import com.example.commentservice.models.Comment;
import com.example.commentservice.repositories.CommentRepository;
import com.example.commentservice.services.CommentService;
import com.example.commonmodule.controllers.TitleBodyController;
import com.example.commonmodule.dtos.PageableBody;
import com.example.commonmodule.dtos.response.PageableResponse;
import com.example.commonmodule.dtos.response.ResponseWithUserDto;
import com.example.commonmodule.dtos.response.ResponseWithUserDtoEntity;
import com.example.commonmodule.dtos.response.ResponseWithUserLikesAndDislikesEntity;
import com.example.commonmodule.enums.Role;
import com.example.commonmodule.hateos.CustomEntityModel;
import com.example.commonmodule.utils.RequestsUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController implements TitleBodyController<Comment, CommentBody, CommentResponse, CommentRepository,
        CommentMapper, CommentService> {

    private final CommentService commentService;
    private final CommentReactiveResponseBuilder commentReactiveResponseBuilder;
    private final RequestsUtils requestsUtils;

    @Override
    @DeleteMapping(value = "/delete/{id}", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<CustomEntityModel<CommentResponse>>> deleteModel(
            @PathVariable Long id, ServerWebExchange exchange) {
        return commentService.deleteModel(id, requestsUtils.extractAuthUser(exchange))
                .flatMap(m -> commentReactiveResponseBuilder.toModel(m, CommentController.class))
                .map(ResponseEntity::ok);
    }

    @Override
    @GetMapping(value = "/{id}", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<CustomEntityModel<CommentResponse>>> getModelById(@PathVariable Long id, ServerWebExchange exchange) {
        return commentService.getModelById(id, requestsUtils.extractAuthUser(exchange))
                .flatMap(m -> commentReactiveResponseBuilder.toModel(m, CommentController.class))
                .map(ResponseEntity::ok);
    }

    @Override
    @GetMapping(value = "/withUser/{id}", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<ResponseWithUserDtoEntity<CommentResponse>>> getModelByIdWithUser(@PathVariable Long id, ServerWebExchange exchange) {
        return commentService.getModelByIdWithUser(id, requestsUtils.extractAuthUser(exchange))
                .flatMap(m -> commentReactiveResponseBuilder.toModelWithUser(m, CommentController.class))
                .map(ResponseEntity::ok);
    }

    @Override
    @PutMapping(value = "/update/{id}", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<CustomEntityModel<CommentResponse>>> updateModel(@Valid @RequestBody CommentBody commentBody, @PathVariable Long id, ServerWebExchange exchange) {
        return commentService.updateModel(id, commentBody, requestsUtils.extractAuthUser(exchange))
                .flatMap(m -> commentReactiveResponseBuilder.toModel(m, CommentController.class))
                .map(ResponseEntity::ok);
    }


    @Override
    @PatchMapping(value = "/like/{id}", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<CustomEntityModel<CommentResponse>>> likeModel(@PathVariable Long id, ServerWebExchange exchange) {
        return commentService.reactToModel(id, "like", requestsUtils.extractAuthUser(exchange))
                .flatMap(m -> commentReactiveResponseBuilder.toModel(m, CommentController.class))
                .map(ResponseEntity::ok);
    }

    @Override
    @PatchMapping(value = "/dislike/{id}", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<CustomEntityModel<CommentResponse>>> dislikeModel(@PathVariable Long id, ServerWebExchange exchange) {
        return commentService.reactToModel(id, "dislike", requestsUtils.extractAuthUser(exchange))
                .flatMap(m -> commentReactiveResponseBuilder.toModel(m, CommentController.class))
                .map(ResponseEntity::ok);
    }

    @Override
    @GetMapping(value = "/withUser/withReactions/{id}", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<ResponseWithUserLikesAndDislikesEntity<CommentResponse>>> getModelsWithUserAndReaction(@PathVariable Long id, ServerWebExchange exchange) {
        return commentService.getModelByIdWithUserLikesAndDislikes(id, requestsUtils.extractAuthUser(exchange))
                .flatMap(m -> commentReactiveResponseBuilder.toModelWithUserLikesAndDislikes(m, CommentController.class))
                .map(ResponseEntity::ok);
    }

    @PostMapping(value = "/create/{postId}", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<CustomEntityModel<CommentResponse>>> createComment(
            @PathVariable Long postId,
            @Valid @RequestBody CommentBody commentBody,
            ServerWebExchange exchange
    ) {
        return commentService.createModel(postId, commentBody, requestsUtils.extractAuthUser(exchange))
                .flatMap(m -> commentReactiveResponseBuilder.toModel(m, CommentController.class))
                .map(ResponseEntity::ok);
    }


    @PatchMapping(value = "/{postId}", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public Flux<PageableResponse<CustomEntityModel<CommentResponse>>> getCommentsByPost(
            @PathVariable Long postId,
            @Valid @RequestBody PageableBody pageableBody


    ) {
        return commentService.getCommentsByPost(postId, pageableBody)
                .flatMap(m -> commentReactiveResponseBuilder.toModelPageable(m, CommentController.class));
    }

    @PatchMapping(value = "/user/{userId}", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public Flux<PageableResponse<CustomEntityModel<CommentResponse>>> getCommentsByUser(
            @PathVariable Long userId,
            @Valid @RequestBody PageableBody pageableBody

    ) {
        return commentService.getModelByUser(userId, pageableBody)
                .flatMap(m -> commentReactiveResponseBuilder.toModelPageable(m, CommentController.class));
    }

    @PatchMapping(value = "/byIds", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public Flux<PageableResponse<CustomEntityModel<CommentResponse>>> getModelsByIdIn(@Valid @RequestBody PageableBody pageableBody,
                                                                                      @RequestParam List<Long> ids) {
        return commentService.getModelsByIdIn(ids, pageableBody)
                .flatMap(m -> commentReactiveResponseBuilder.toModelPageable(m, CommentController.class));
    }

    @PatchMapping(value = "/withUser/byPost/{postId}", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public Flux<PageableResponse<ResponseWithUserDtoEntity<CommentResponse>>> getCommentsWithUserByPost(
            @PathVariable Long postId,
            @Valid @RequestBody PageableBody pageableBody
    ) {
        return commentService.getCommentsWithUserByPost(postId, pageableBody)
                .flatMap(m -> commentReactiveResponseBuilder.toModelWithUserPageable(m, CommentController.class));
    }

    @DeleteMapping(value = "/admin/delete/post/{postId}", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<Void>> deleteCommentsByPostAdmin(@PathVariable Long postId, ServerWebExchange exchange) {
        return commentService.deleteCommentsByPost(postId, requestsUtils.extractAuthUser(exchange), Role.ROLE_ADMIN)
                .then(Mono.just(ResponseEntity.ok().build()));
    }

    @DeleteMapping(value = "/internal/post/{postId}", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<Void>> deleteCommentsByPostInternal(@PathVariable Long postId, ServerWebExchange exchange) {
        return commentService.deleteCommentsByPost(postId, requestsUtils.extractAuthUser(exchange), Role.ROLE_TRAINER)
                .then(Mono.just(ResponseEntity.ok().build()));
    }

    @GetMapping(value = "/internal/post/{postId}", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Flux<ResponseWithUserDto<CommentResponse>> getCommentsByPostInternal(@PathVariable Long postId) {
        return commentService.getCommentsByPost(postId);
    }

}
