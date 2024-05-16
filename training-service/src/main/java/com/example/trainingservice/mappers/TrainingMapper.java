package com.example.trainingservice.mappers;

import com.example.commonmodule.mappers.DtoMapper;
import com.example.commonmodule.utils.EntitiesUtils;
import com.example.trainingservice.clients.ExerciseClient;
import com.example.trainingservice.dto.TrainingBody;
import com.example.trainingservice.dto.TrainingResponse;
import com.example.trainingservice.dto.TrainingResponseWithOrderCount;
import com.example.trainingservice.dto.TrainingWithOrderCount;
import com.example.trainingservice.models.Training;

import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

@Mapper(componentModel = "spring")
@Slf4j
public abstract class TrainingMapper extends DtoMapper<Training, TrainingBody, TrainingResponse> {

    @Autowired
    private ExerciseClient exerciseClient;


    @Override
    public Mono<Training> updateModelFromBody(TrainingBody body, Training training) {
        return exerciseClient.verifyMappingExercises(body.getExercises())
                .then(Mono.fromCallable(
                        () -> {
                            training.setBody(body.getBody());
                            training.setTitle(body.getTitle());
                            training.setExercises(
                                    body.getExercises()
                                            .stream().distinct().toList()
                            );
                            training.setPrice(body.getPrice());
                            training.setApproved(false);
                            training.setUserDislikes(new ArrayList<>());
                            training.setUserLikes(new ArrayList<>());
                            training.setImages(body.getImages());
                            return training;
                        }
                ));
    }

    public abstract TrainingResponseWithOrderCount fromModelToResponseWithOrderCount(TrainingWithOrderCount trainingWithOrderCount);
}
