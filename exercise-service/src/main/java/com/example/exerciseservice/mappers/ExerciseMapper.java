package com.example.exerciseservice.mappers;


import com.example.commonmodule.mappers.DtoMapper;
import com.example.exerciseservice.dtos.ExerciseBody;
import com.example.exerciseservice.dtos.ExerciseResponse;
import com.example.exerciseservice.dtos.ExerciseResponseWithTrainingCount;
import com.example.exerciseservice.dtos.ExerciseWithTrainingCount;
import com.example.exerciseservice.models.Exercise;
import org.mapstruct.Mapper;
import reactor.core.publisher.Mono;

@Mapper(componentModel = "spring")
public abstract class ExerciseMapper extends DtoMapper<Exercise, ExerciseBody, ExerciseResponse> {

    @Override
    public Mono<Exercise> updateModelFromBody(ExerciseBody body, Exercise exercise) {
        exercise.setMuscleGroups(body.getMuscleGroups());
        exercise.setTitle(body.getTitle());
        exercise.setBody(body.getBody());
        exercise.setApproved(false);
        exercise.setImages(body.getImages());
        exercise.setVideos(body.getVideos());
        return Mono.just(exercise);
    }

    public abstract ExerciseResponseWithTrainingCount fromModelToResponseWithTrainingCount(ExerciseWithTrainingCount exerciseWithTrainingCount);

}
