package com.example.postservice.controllers;

import com.example.commonmodule.controllers.ApproveController;
import com.example.commonmodule.dtos.PageableBody;
import com.example.commonmodule.dtos.response.*;
import com.example.commonmodule.hateos.CustomEntityModel;
import com.example.commonmodule.utils.RequestsUtils;
import com.example.postservice.dtos.PostBody;
import com.example.postservice.dtos.PostResponse;
import com.example.postservice.dtos.comments.CommentResponse;
import com.example.postservice.hateos.PostReactiveResponseBuilder;
import com.example.postservice.mappers.PostMapper;
import com.example.postservice.models.Post;
import com.example.postservice.repositories.PostRepository;
import com.example.postservice.services.PostService;
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
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController implements ApproveController
        <Post, PostBody, PostResponse,
                PostRepository, PostMapper,
                PostService> {

    private final PostService postService;
    private final PostReactiveResponseBuilder postReactiveResponseBuilder;
    private final RequestsUtils requestsUtils;

    @Override
    @PatchMapping(value = "/approved", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public Flux<PageableResponse<CustomEntityModel<PostResponse>>> getModelsApproved(
            @RequestParam(required = false) String title, @Valid @RequestBody PageableBody pageableBody, ServerWebExchange exchange
    ) {
        return postService.getModelsApproved(title, pageableBody, requestsUtils.extractAuthUser(exchange))
                .flatMap(m -> postReactiveResponseBuilder.toModelPageable(m, PostController.class));
    }

    @Override
    @PatchMapping(value = "/trainer/{trainerId}", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public Flux<PageableResponse<CustomEntityModel<PostResponse>>> getModelsTrainer(@RequestParam(required = false) String title, @Valid @RequestBody PageableBody pageableBody, @PathVariable Long trainerId, ServerWebExchange exchange) {
        return postService.getModelsTrainer(title, trainerId, pageableBody, requestsUtils.extractAuthUser(exchange))
                .flatMap(m -> postReactiveResponseBuilder.toModelPageable(m, PostController.class));
    }

    @Override
    @PostMapping(value = "/create", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<CustomEntityModel<PostResponse>>> createModel(@Valid @RequestBody PostBody body, ServerWebExchange exchange) {
        return postService.createModel(body, requestsUtils.extractAuthUser(exchange))
                .flatMap(m -> postReactiveResponseBuilder.toModel(m, PostController.class))
                .map(ResponseEntity::ok);
    }

    @Override
    @PatchMapping(value = "/admin/approve/{id}", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<CustomEntityModel<PostResponse>>> approveModel(@PathVariable Long id, ServerWebExchange exchange) {
        return postService.approveModel(id, requestsUtils.extractAuthUser(exchange))
                .flatMap(m -> postReactiveResponseBuilder.toModel(m, PostController.class))
                .map(ResponseEntity::ok);
    }

    @Override
    @PatchMapping(value = "/admin", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public Flux<PageableResponse<CustomEntityModel<PostResponse>>> getAllModelsAdmin(@RequestParam(required = false) String title, @Valid @RequestBody PageableBody pageableBody, ServerWebExchange exchange) {
        return postService.getAllModels(title, pageableBody, requestsUtils.extractAuthUser(exchange))
                .flatMap(m -> postReactiveResponseBuilder.toModelPageable(m, PostController.class));
    }

    @Override
    @DeleteMapping(value = "/delete/{id}", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<CustomEntityModel<PostResponse>>> deleteModel(@PathVariable Long id, ServerWebExchange exchange) {
        return postService.deleteModel(id, requestsUtils.extractAuthUser(exchange))
                .flatMap(m -> postReactiveResponseBuilder.toModel(m, PostController.class))
                .map(ResponseEntity::ok);
    }

    @Override
    @GetMapping(value = "/{id}", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<CustomEntityModel<PostResponse>>> getModelById(@PathVariable Long id, ServerWebExchange exchange) {
        return postService.getModelById(id, requestsUtils.extractAuthUser(exchange))
                .flatMap(m -> postReactiveResponseBuilder.toModel(m, PostController.class))
                .map(ResponseEntity::ok);
    }

    @Override
    @GetMapping(value = "/withUser/{id}", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<ResponseWithUserDtoEntity<PostResponse>>> getModelByIdWithUser(@PathVariable Long id, ServerWebExchange exchange) {
        return postService.getModelByIdWithUser(id, requestsUtils.extractAuthUser(exchange))
                .flatMap(m -> postReactiveResponseBuilder.toModelWithUser(m, PostController.class))
                .map(ResponseEntity::ok);
    }

    @Override
    @PutMapping(value = "/update/{id}", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<CustomEntityModel<PostResponse>>> updateModel(@Valid @RequestBody PostBody postBody, @PathVariable Long id, ServerWebExchange exchange) {
        return postService.updateModel(id, postBody, requestsUtils.extractAuthUser(exchange))
                .flatMap(m -> postReactiveResponseBuilder.toModel(m, PostController.class))
                .map(ResponseEntity::ok);
    }

    @Override
    @PatchMapping(value = "/byIds", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public Flux<PageableResponse<CustomEntityModel<PostResponse>>> getModelsByIdIn(@Valid @RequestBody PageableBody pageableBody,
                                                                                   @RequestParam List<Long> ids) {
        return postService.getModelsByIdIn(ids, pageableBody)
                .flatMap(m -> postReactiveResponseBuilder.toModelPageable(m, PostController.class));
    }

    @Override
    @PatchMapping(value = "/like/{id}", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<CustomEntityModel<PostResponse>>> likeModel(@PathVariable Long id, ServerWebExchange exchange) {
        return postService.reactToModel(id, "like", requestsUtils.extractAuthUser(exchange))
                .flatMap(m -> postReactiveResponseBuilder.toModel(m, PostController.class))
                .map(ResponseEntity::ok);
    }

    @Override
    @PatchMapping(value = "/dislike/{id}", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<CustomEntityModel<PostResponse>>> dislikeModel(@PathVariable Long id, ServerWebExchange exchange) {
        return postService.reactToModel(id, "dislike", requestsUtils.extractAuthUser(exchange))
                .flatMap(m -> postReactiveResponseBuilder.toModel(m, PostController.class))
                .map(ResponseEntity::ok);
    }

    @Override
    @GetMapping(value = "/withUser/withReactions/{id}", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<ResponseWithUserLikesAndDislikesEntity<PostResponse>>> getModelsWithUserAndReaction(@PathVariable Long id, ServerWebExchange exchange) {
        return postService.getModelByIdWithUserLikesAndDislikes(id, requestsUtils.extractAuthUser(exchange))
                .flatMap(m -> postReactiveResponseBuilder.toModelWithUserLikesAndDislikes(m, PostController.class))
                .map(ResponseEntity::ok);
    }

    @GetMapping(value = "/internal/existsApproved/{id}", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<Void>> existsById(@PathVariable Long id) {
        return postService.existsByIdAndApprovedIsTrue(id)
                .then(Mono.fromCallable(() -> ResponseEntity.noContent().build()));
    }

    @GetMapping(value = "/withComments/{id}", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<ResponseWithChildListEntity<PostResponse, ResponseWithUserDto<CommentResponse>>>>
    getPostWithComments(@PathVariable Long id) {
        return postService.getPostWithComments(id, true)
                .map(m -> new ResponseWithChildListEntity<>(CustomEntityModel.of(m.getEntity()), m.getChildren()))
                .map(ResponseEntity::ok);
    }
}
