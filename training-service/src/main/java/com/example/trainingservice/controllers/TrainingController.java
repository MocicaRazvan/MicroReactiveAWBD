package com.example.trainingservice.controllers;

import com.example.commonmodule.controllers.ApproveController;
import com.example.commonmodule.controllers.ValidControllerIds;
import com.example.commonmodule.dtos.PageableBody;
import com.example.commonmodule.dtos.response.*;
import com.example.commonmodule.hateos.CustomEntityModel;
import com.example.commonmodule.utils.RequestsUtils;
import com.example.trainingservice.dto.TrainingBody;
import com.example.trainingservice.dto.TrainingResponse;
import com.example.trainingservice.dto.TrainingResponseWithOrderCount;
import com.example.trainingservice.dto.exercises.ExerciseResponse;
import com.example.trainingservice.dto.orders.TotalPrice;
import com.example.trainingservice.hateos.TrainingReactiveResponseBuilder;
import com.example.trainingservice.mappers.TrainingMapper;
import com.example.trainingservice.models.Training;
import com.example.trainingservice.repositories.TrainingRepository;
import com.example.trainingservice.services.TrainingService;
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
@RequestMapping("/trainings")
@RequiredArgsConstructor
public class TrainingController implements ApproveController
        <Training, TrainingBody, TrainingResponse, TrainingRepository, TrainingMapper,
                TrainingService>, ValidControllerIds {


    private final TrainingService trainingService;
    private final RequestsUtils requestsUtils;
    private final TrainingReactiveResponseBuilder trainingReactiveResponseBuilder;

    @Override
    @PatchMapping(value = "/approved", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public Flux<PageableResponse<CustomEntityModel<TrainingResponse>>> getModelsApproved(
            @RequestParam(required = false) String title, @Valid @RequestBody PageableBody pageableBody, ServerWebExchange exchange) {
        return trainingService.getModelsApproved(title, pageableBody, requestsUtils.extractAuthUser(exchange))
                .flatMap(m -> trainingReactiveResponseBuilder.toModelPageable(m, TrainingController.class));
    }

    @Override
    @PatchMapping(value = "/trainer/{trainerId}", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public Flux<PageableResponse<CustomEntityModel<TrainingResponse>>> getModelsTrainer(
            @RequestParam(required = false) String title, @Valid @RequestBody PageableBody pageableBody, @PathVariable Long trainerId, ServerWebExchange exchange) {
        return trainingService.getModelsTrainer(title, trainerId, pageableBody, requestsUtils.extractAuthUser(exchange))
                .flatMap(m -> trainingReactiveResponseBuilder.toModelPageable(m, TrainingController.class));
    }

    @Override
    @PostMapping(value = "/create", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<CustomEntityModel<TrainingResponse>>> createModel(@Valid @RequestBody TrainingBody body, ServerWebExchange exchange) {
        return trainingService.createModel(body, requestsUtils.extractAuthUser(exchange))
                .flatMap(m -> trainingReactiveResponseBuilder.toModel(m, TrainingController.class))
                .map(ResponseEntity::ok);
    }

    @Override
    @PatchMapping(value = "/admin/approve/{id}", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<CustomEntityModel<TrainingResponse>>> approveModel(@PathVariable Long id, ServerWebExchange exchange) {
        return trainingService.approveModel(id, requestsUtils.extractAuthUser(exchange))
                .flatMap(m -> trainingReactiveResponseBuilder.toModel(m, TrainingController.class))
                .map(ResponseEntity::ok);
    }

    @Override
    @PatchMapping(value = "/admin", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public Flux<PageableResponse<CustomEntityModel<TrainingResponse>>> getAllModelsAdmin(@RequestParam(required = false) String title, @Valid PageableBody pageableBody, ServerWebExchange exchange) {
        return trainingService.getAllModels(title, pageableBody, requestsUtils.extractAuthUser(exchange))
                .flatMap(m -> trainingReactiveResponseBuilder.toModelPageable(m, TrainingController.class));
    }

    @Override
    @DeleteMapping(value = "/delete/{id}", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<CustomEntityModel<TrainingResponse>>> deleteModel(@PathVariable Long id, ServerWebExchange exchange) {
        return trainingService.deleteModel(id, requestsUtils.extractAuthUser(exchange))
                .flatMap(m -> trainingReactiveResponseBuilder.toModel(m, TrainingController.class))
                .map(ResponseEntity::ok);
    }

    @Override
    @GetMapping(value = "/{id}", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<CustomEntityModel<TrainingResponse>>> getModelById(@PathVariable Long id, ServerWebExchange exchange) {
        return trainingService.getModelById(id, requestsUtils.extractAuthUser(exchange))
                .flatMap(m -> trainingReactiveResponseBuilder.toModel(m, TrainingController.class))
                .map(ResponseEntity::ok);
    }

    @Override
    @GetMapping(value = "/withUser/{id}", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<ResponseWithUserDtoEntity<TrainingResponse>>> getModelByIdWithUser(@PathVariable Long id, ServerWebExchange exchange) {
        return trainingService.getModelByIdWithUser(id, requestsUtils.extractAuthUser(exchange))
                .flatMap(t -> trainingReactiveResponseBuilder.toModelWithUser(t, TrainingController.class))
                .map(ResponseEntity::ok);
    }

    @Override
    @PutMapping(value = "/update/{id}", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<CustomEntityModel<TrainingResponse>>> updateModel(@Valid @RequestBody TrainingBody trainingBody, @PathVariable Long id, ServerWebExchange exchange) {
        return trainingService.updateModel(id, trainingBody, requestsUtils.extractAuthUser(exchange))
                .flatMap(m -> trainingReactiveResponseBuilder.toModel(m, TrainingController.class))
                .map(ResponseEntity::ok);
    }

    @Override
    @PatchMapping(value = "/byIds", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Flux<PageableResponse<CustomEntityModel<TrainingResponse>>> getModelsByIdIn(@Valid @RequestBody PageableBody pageableBody, @RequestParam List<Long> ids) {
        return trainingService.getModelsByIdIn(ids, pageableBody)
                .flatMap(m -> trainingReactiveResponseBuilder.toModelPageable(m, TrainingController.class));
    }

    @Override
    @PatchMapping(value = "/like/{id}", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<CustomEntityModel<TrainingResponse>>> likeModel(@PathVariable Long id, ServerWebExchange exchange) {
        return trainingService.reactToModel(id, "like", requestsUtils.extractAuthUser(exchange))
                .flatMap(m -> trainingReactiveResponseBuilder.toModel(m, TrainingController.class))
                .map(ResponseEntity::ok);
    }

    @Override
    @PatchMapping(value = "/dislike/{id}", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<CustomEntityModel<TrainingResponse>>> dislikeModel(@PathVariable Long id, ServerWebExchange exchange) {
        return trainingService.reactToModel(id, "dislike", requestsUtils.extractAuthUser(exchange))
                .flatMap(m -> trainingReactiveResponseBuilder.toModel(m, TrainingController.class))
                .map(ResponseEntity::ok);
    }

    @Override
    @GetMapping(value = "/withUser/withReactions/{id}", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<ResponseWithUserLikesAndDislikesEntity<TrainingResponse>>> getModelsWithUserAndReaction(@PathVariable Long id, ServerWebExchange exchange) {
        return trainingService.getModelByIdWithUserLikesAndDislikes(id, requestsUtils.extractAuthUser(exchange))
                .flatMap(t -> trainingReactiveResponseBuilder.toModelWithUserLikesAndDislikes(t, TrainingController.class))
                .map(ResponseEntity::ok);
    }

    @GetMapping(value = "/withExercises", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Flux<ResponseWithChildListEntity<TrainingResponse, ResponseWithUserDto<ExerciseResponse>>> getTrainingsWithExercises(@RequestParam List<Long> ids) {
        return trainingService.getTrainingsWithExercises(ids, true)
                .flatMap(t -> trainingReactiveResponseBuilder.toModel(t.getEntity(), TrainingController.class)
                        .map(m -> new ResponseWithChildListEntity<>(m, t.getChildren())
                        ));
    }

    @GetMapping(value = "/withExercises/{id}", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<ResponseWithChildListEntity<TrainingResponse, ResponseWithUserDto<ExerciseResponse>>>> getTrainingWithExercises(@PathVariable Long id) {
        return trainingService.getTrainingWithExercises(id, true)
                .flatMap(t -> trainingReactiveResponseBuilder.toModel(t.getEntity(), TrainingController.class)
                        .map(m -> new ResponseWithChildListEntity<>(m, t.getChildren())
                        ))
                .map(ResponseEntity::ok);
    }

    @GetMapping(value = "/withOrderCount/{id}", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<TrainingResponseWithOrderCount>> getTrainingWithOrderCount(@PathVariable Long id, ServerWebExchange exchange) {
        return trainingService.getTrainingWithOrderCount(id, requestsUtils.extractAuthUser(exchange))
                .map(ResponseEntity::ok);
    }

    @Override
    @GetMapping(value = "/internal/validIds", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<Void>> validIds(@RequestParam List<Long> ids) {
        return trainingService.validIds(ids)
                .then(Mono.fromCallable(() -> ResponseEntity.noContent().build()));
    }

    @GetMapping(value = "/internal/exerciseCount/{exerciseId}", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<EntityCount>> getExerciseInTrainingsCount(@PathVariable Long exerciseId) {
        return trainingService.getExerciseInTrainingsCount(exerciseId)
                .map(ResponseEntity::ok);
    }

    @GetMapping(value = "/internal/totalPrice", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Mono<ResponseEntity<TotalPrice>> getTotalPriceById(@RequestParam List<Long> ids) {
        return trainingService.getTotalPriceById(ids)
                .map(ResponseEntity::ok);
    }

    @GetMapping(value = "/internal/byIds", produces = {MediaType.APPLICATION_NDJSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Flux<CustomEntityModel<TrainingResponse>> getInternalModels(@RequestParam List<Long> ids) {
        return trainingService.getModelsByIdIn(ids)
                .flatMap(m -> trainingReactiveResponseBuilder.toModel(m, TrainingController.class));
    }

}
