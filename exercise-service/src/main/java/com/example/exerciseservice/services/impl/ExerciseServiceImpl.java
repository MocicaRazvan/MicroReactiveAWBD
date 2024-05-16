package com.example.exerciseservice.services.impl;

import com.example.commonmodule.dtos.UserDto;
import com.example.commonmodule.dtos.response.EntityCount;
import com.example.commonmodule.dtos.response.ResponseWithUserDto;
import com.example.commonmodule.exceptions.action.SubEntityUsed;
import com.example.commonmodule.services.ValidIds;
import com.example.commonmodule.services.impl.ApprovedServiceImpl;
import com.example.commonmodule.utils.EntitiesUtils;
import com.example.commonmodule.utils.PageableUtilsCustom;
import com.example.commonmodule.utils.UserUtils;
import com.example.exerciseservice.clients.TrainingClient;
import com.example.exerciseservice.dtos.ExerciseBody;
import com.example.exerciseservice.dtos.ExerciseResponse;
import com.example.exerciseservice.dtos.ExerciseResponseWithTrainingCount;
import com.example.exerciseservice.dtos.ExerciseTrainingCount;
import com.example.exerciseservice.mappers.ExerciseMapper;
import com.example.exerciseservice.models.Exercise;
import com.example.exerciseservice.repositories.ExerciseRepository;
import com.example.exerciseservice.services.ExerciseService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class ExerciseServiceImpl extends ApprovedServiceImpl<Exercise, ExerciseBody, ExerciseResponse, ExerciseRepository, ExerciseMapper>
        implements ExerciseService, ValidIds {


    private final TrainingClient trainingClient;

    public ExerciseServiceImpl(ExerciseRepository modelRepository, ExerciseMapper modelMapper, PageableUtilsCustom pageableUtils, UserUtils userUtils, EntitiesUtils entitiesUtils, TrainingClient trainingClient) {
        super(modelRepository, modelMapper, pageableUtils, userUtils, "exercise", List.of("id", "userId", "postId", "title"), entitiesUtils);
        this.trainingClient = trainingClient;
    }


    @Override
    public Mono<ExerciseResponse> deleteModel(Long id, String userId) {
        return validateExerciseNotUsed(id)
                .then(super.deleteModel(id, userId));
    }

    @Override
    public Mono<ExerciseResponse> updateModel(Long id, ExerciseBody body, String userId) {
        return validateExerciseNotUsed(id)
                .then(super.updateModel(id, body, userId));
    }

    @Override
    public Flux<ResponseWithUserDto<ExerciseResponse>> getExercisesWithUserByIds(List<Long> ids) {
        return modelRepository.findAllById(ids)
                .flatMap(model -> userUtils.getUser("", model.getUserId().toString())
                        .map(user -> ResponseWithUserDto.<ExerciseResponse>builder()
                                .model(modelMapper.fromModelToResponse(model))
                                .user(user)
                                .build()));
    }

    @Override
    public Mono<Void> validIds(List<Long> ids) {
        return entitiesUtils.validIds(ids, modelRepository, modelName);
    }

    @Override
    public Mono<EntityCount> getTrainingCount(Long id) {
        return trainingClient.getExerciseInTrainingsCount(id.toString());
    }

    @Override
    public Mono<ExerciseResponseWithTrainingCount> getExerciseWithTrainingCount(Long id, String userId) {
        return getModelByIdWithOwner(id, userId)
                .zipWith(getTrainingCount(id))
                .map(tuple -> {
                    ExerciseResponse exercise = tuple.getT1().getT1();
                    EntityCount count = tuple.getT2();
                    UserDto user = tuple.getT1().getT2();
                    return new ExerciseResponseWithTrainingCount()
                            .fromResponse(exercise, count.getCount(), user);
                });
    }

    @Override
    public Flux<ExerciseResponse> getApprovedModelsTrainer(Long trainerId) {
        return userUtils.existsTrainerOrAdmin("/exists", trainerId)
                .thenMany(modelRepository.findAllByUserIdAndApprovedTrue(trainerId)
                        .map(modelMapper::fromModelToResponse));
    }

    private Mono<Void> validateExerciseNotUsed(Long id) {
        return trainingClient.getExerciseInTrainingsCount(id.toString())
                .flatMap(count -> {
                    if (count.getCount() > 0) {
                        return Mono.error(new SubEntityUsed("exercise", id));
                    }
                    return Mono.empty();
                });
    }
}

