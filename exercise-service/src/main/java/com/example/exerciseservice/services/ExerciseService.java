package com.example.exerciseservice.services;


import com.example.commonmodule.dtos.response.EntityCount;
import com.example.commonmodule.dtos.response.ResponseWithUserDto;
import com.example.commonmodule.services.ApprovedService;
import com.example.commonmodule.services.ValidIds;
import com.example.exerciseservice.dtos.ExerciseBody;
import com.example.exerciseservice.dtos.ExerciseResponse;
import com.example.exerciseservice.dtos.ExerciseResponseWithTrainingCount;
import com.example.exerciseservice.dtos.ExerciseTrainingCount;
import com.example.exerciseservice.mappers.ExerciseMapper;
import com.example.exerciseservice.models.Exercise;
import com.example.exerciseservice.repositories.ExerciseRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ExerciseService extends ApprovedService
        <Exercise, ExerciseBody, ExerciseResponse, ExerciseRepository, ExerciseMapper>, ValidIds {

    Flux<ResponseWithUserDto<ExerciseResponse>> getExercisesWithUserByIds(List<Long> ids);

    Mono<Void> validIds(List<Long> ids);

    Mono<EntityCount> getTrainingCount(Long id);

    Mono<ExerciseResponseWithTrainingCount> getExerciseWithTrainingCount(Long id, String userId);

    Flux<ExerciseResponse> getApprovedModelsTrainer(Long trainerId);
    

}
