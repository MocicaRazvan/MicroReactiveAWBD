package com.example.trainingservice.services;


import com.example.commonmodule.dtos.response.EntityCount;
import com.example.commonmodule.dtos.response.ResponseWithChildList;
import com.example.commonmodule.dtos.response.ResponseWithUserDto;
import com.example.commonmodule.services.ApprovedService;
import com.example.commonmodule.services.ValidIds;
import com.example.trainingservice.dto.TrainingBody;
import com.example.trainingservice.dto.TrainingResponse;
import com.example.trainingservice.dto.TrainingResponseWithOrderCount;
import com.example.trainingservice.dto.exercises.ExerciseResponse;
import com.example.trainingservice.dto.orders.TotalPrice;
import com.example.trainingservice.mappers.TrainingMapper;
import com.example.trainingservice.models.Training;

import com.example.trainingservice.repositories.TrainingRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface TrainingService extends ApprovedService<Training, TrainingBody, TrainingResponse,
        TrainingRepository, TrainingMapper>, ValidIds {
// trainings with exercises

    Mono<ResponseWithChildList<TrainingResponse, ResponseWithUserDto<ExerciseResponse>>>
    getTrainingWithExercises(Long id, boolean approved);

    Flux<ResponseWithChildList<TrainingResponse, ResponseWithUserDto<ExerciseResponse>>>
    getTrainingsWithExercises(List<Long> ids, boolean approved);

    Mono<TotalPrice> getTotalPriceById(List<Long> ids);

    Mono<TrainingResponseWithOrderCount> getTrainingWithOrderCount(Long id, String userId);

    Mono<EntityCount> getExerciseInTrainingsCount(Long exerciseId);

    Mono<EntityCount> getTrainingInOrdersCount(Long trainingId);
}
