package com.example.exerciseservice.controllers;

import com.example.commonmodule.controllers.ApproveController;
import com.example.commonmodule.controllers.ValidControllerIds;
import com.example.commonmodule.dtos.PageableBody;
import com.example.commonmodule.dtos.response.EntityCount;
import com.example.commonmodule.dtos.response.PageableResponse;
import com.example.commonmodule.dtos.response.ResponseWithUserDtoEntity;
import com.example.commonmodule.dtos.response.ResponseWithUserLikesAndDislikesEntity;
import com.example.commonmodule.hateos.CustomEntityModel;
import com.example.commonmodule.utils.RequestsUtils;
import com.example.exerciseservice.dtos.ExerciseBody;
import com.example.exerciseservice.dtos.ExerciseResponse;
import com.example.exerciseservice.dtos.ExerciseResponseWithTrainingCount;
import com.example.exerciseservice.dtos.ExerciseTrainingCount;
import com.example.exerciseservice.hateos.ExerciseReactiveResponseBuilder;
import com.example.exerciseservice.mappers.ExerciseMapper;
import com.example.exerciseservice.models.Exercise;
import com.example.exerciseservice.repositories.ExerciseRepository;
import com.example.exerciseservice.services.ExerciseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/exercises")
@RequiredArgsConstructor
@Slf4j
public class ExerciseController implements ApproveController
        <Exercise, ExerciseBody, ExerciseResponse, ExerciseRepository, ExerciseMapper,
                ExerciseService>, ValidControllerIds {

    private final ExerciseService exerciseService;
    private final ExerciseReactiveResponseBuilder exerciseReactiveResponseBuilder;
    private final RequestsUtils requestsUtils;

    @Override
    @PatchMapping(value = "/approved", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public Flux<PageableResponse<CustomEntityModel<ExerciseResponse>>> getModelsApproved(
            @RequestParam(required = false) String title, @Valid @RequestBody PageableBody pageableBody, ServerWebExchange exchange
    ) {
        return exerciseService.getModelsApproved(title, pageableBody, requestsUtils.extractAuthUser(exchange))
                .flatMap(e -> exerciseReactiveResponseBuilder.toModelPageable(e, ExerciseController.class));
    }

    @Override
    @PatchMapping(value = "/trainer/{trainerId}", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public Flux<PageableResponse<CustomEntityModel<ExerciseResponse>>> getModelsTrainer(
            @RequestParam(required = false) String title, @Valid @RequestBody PageableBody pageableBody, @PathVariable Long trainerId, ServerWebExchange exchange) {
        return exerciseService.getModelsTrainer(title, trainerId, pageableBody, requestsUtils.extractAuthUser(exchange))
                .flatMap(e -> exerciseReactiveResponseBuilder.toModelPageable(e, ExerciseController.class));
    }

    @Override
    @PostMapping(value = "/create", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<CustomEntityModel<ExerciseResponse>>> createModel(@Valid @RequestBody ExerciseBody body, ServerWebExchange exchange) {
        return exerciseService.createModel(body, requestsUtils.extractAuthUser(exchange))
                .flatMap(e -> exerciseReactiveResponseBuilder.toModel(e, ExerciseController.class))
                .map(ResponseEntity::ok);
    }

    @Override
    @PatchMapping(value = "/admin/approve/{id}", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<CustomEntityModel<ExerciseResponse>>> approveModel(@PathVariable Long id, ServerWebExchange exchange) {
        return exerciseService.approveModel(id, requestsUtils.extractAuthUser(exchange))
                .flatMap(e -> exerciseReactiveResponseBuilder.toModel(e, ExerciseController.class))
                .map(ResponseEntity::ok);
    }

    @Override
    @PatchMapping(value = "/admin", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public Flux<PageableResponse<CustomEntityModel<ExerciseResponse>>> getAllModelsAdmin(
            @RequestParam(required = false) String title, @Valid @RequestBody PageableBody pageableBody, ServerWebExchange exchange) {
        return exerciseService.getAllModels(title, pageableBody, requestsUtils.extractAuthUser(exchange))
                .flatMap(e -> exerciseReactiveResponseBuilder.toModelPageable(e, ExerciseController.class));
    }

    @Override
    @DeleteMapping(value = "/delete/{id}", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<CustomEntityModel<ExerciseResponse>>> deleteModel(@PathVariable Long id, ServerWebExchange exchange) {
        return exerciseService.deleteModel(id, requestsUtils.extractAuthUser(exchange))
                .flatMap(e -> exerciseReactiveResponseBuilder.toModel(e, ExerciseController.class))
                .map(ResponseEntity::ok);
    }

    @Override
    @GetMapping(value = "/{id}", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<CustomEntityModel<ExerciseResponse>>> getModelById(@PathVariable Long id, ServerWebExchange exchange) {
        return exerciseService.getModelById(id, requestsUtils.extractAuthUser(exchange))
                .flatMap(e -> exerciseReactiveResponseBuilder.toModel(e, ExerciseController.class))
                .map(ResponseEntity::ok);
    }

    @Override
    @GetMapping(value = "/withUser/{id}", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<ResponseWithUserDtoEntity<ExerciseResponse>>> getModelByIdWithUser(@PathVariable Long id, ServerWebExchange exchange) {
        return exerciseService.getModelByIdWithUser(id, requestsUtils.extractAuthUser(exchange))
                .flatMap(e -> exerciseReactiveResponseBuilder.toModelWithUser(e, ExerciseController.class))
                .map(ResponseEntity::ok);
    }

    @Override
    @PutMapping(value = "/update/{id}", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<CustomEntityModel<ExerciseResponse>>> updateModel(@Valid @RequestBody ExerciseBody exerciseBody, @PathVariable Long id, ServerWebExchange exchange) {
        return exerciseService.updateModel(id, exerciseBody, requestsUtils.extractAuthUser(exchange))
                .flatMap(e -> exerciseReactiveResponseBuilder.toModel(e, ExerciseController.class))
                .map(ResponseEntity::ok);
    }

    @Override
    @PatchMapping(value = "/byIds", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public Flux<PageableResponse<CustomEntityModel<ExerciseResponse>>> getModelsByIdIn(@Valid @RequestBody PageableBody pageableBody,
                                                                                       @RequestParam List<Long> ids) {
        return exerciseService.getModelsByIdIn(ids, pageableBody)
                .flatMap(e -> exerciseReactiveResponseBuilder.toModelPageable(e, ExerciseController.class));
    }

    @Override
    @PatchMapping(value = "/like/{id}", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<CustomEntityModel<ExerciseResponse>>> likeModel(@PathVariable Long id, ServerWebExchange exchange) {
        return exerciseService.reactToModel(id, "like", requestsUtils.extractAuthUser(exchange))
                .flatMap(e -> exerciseReactiveResponseBuilder.toModel(e, ExerciseController.class))
                .map(ResponseEntity::ok);
    }

    @Override
    @PatchMapping(value = "/dislike/{id}", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<CustomEntityModel<ExerciseResponse>>> dislikeModel(@PathVariable Long id, ServerWebExchange exchange) {
        return exerciseService.reactToModel(id, "dislike", requestsUtils.extractAuthUser(exchange))
                .flatMap(e -> exerciseReactiveResponseBuilder.toModel(e, ExerciseController.class))
                .map(ResponseEntity::ok);
    }

    @Override
    @GetMapping(value = "/withUser/withReactions/{id}", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<ResponseWithUserLikesAndDislikesEntity<ExerciseResponse>>> getModelsWithUserAndReaction(@PathVariable Long id, ServerWebExchange exchange) {
        return exerciseService.getModelByIdWithUserLikesAndDislikes(id, requestsUtils.extractAuthUser(exchange))
                .flatMap(e -> exerciseReactiveResponseBuilder.toModelWithUserLikesAndDislikes(e, ExerciseController.class))
                .map(ResponseEntity::ok);
    }

    @GetMapping(value = "/count/{id}", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<EntityCount>> getTrainingCount(@PathVariable Long id) {
        return exerciseService.getTrainingCount(id)
                .map(ResponseEntity::ok);
    }

    @GetMapping(value = "/withTrainingCount/{id}", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<ExerciseResponseWithTrainingCount>> getExerciseWithTrainingCount(@PathVariable Long id, ServerWebExchange exchange) {
        return exerciseService.getExerciseWithTrainingCount(id, requestsUtils.extractAuthUser(exchange))
                .map(ResponseEntity::ok);
    }

    @GetMapping(value = "/approved/trainer/{trainerId}", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Flux<CustomEntityModel<ExerciseResponse>> getApprovedModelsTrainer(@PathVariable Long trainerId) {
        return exerciseService.getApprovedModelsTrainer(trainerId)
                .flatMap(e -> exerciseReactiveResponseBuilder.toModel(e, ExerciseController.class));
    }

    @Override
    @GetMapping(value = "/internal/validIds", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<Void>> validIds(@RequestParam List<Long> ids) {
        return exerciseService.validIds(ids)
                .then(Mono.fromCallable(() -> ResponseEntity.noContent().build()));
    }

    @GetMapping(value = "/internal/byIds/withUser", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Flux<ResponseWithUserDtoEntity<ExerciseResponse>> getModelsByIdInWithUser(@RequestParam List<Long> ids) {
        return exerciseService.getExercisesWithUserByIds(ids)
                .flatMap(e -> exerciseReactiveResponseBuilder.toModelWithUser(e, ExerciseController.class));
    }

    @GetMapping(value = "/internal/byIds", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Flux<CustomEntityModel<ExerciseResponse>> getModelsByIdIn(@RequestParam List<Long> ids) {
        return exerciseService.getModelsByIdIn(ids)
                .flatMap(e -> exerciseReactiveResponseBuilder.toModel(e, ExerciseController.class));
    }
}
