package com.example.exerciseservice.hateos;


import com.example.commonmodule.hateos.controllerMaybe.ReactiveResponseBuilder;
import com.example.exerciseservice.controllers.ExerciseController;
import com.example.exerciseservice.dtos.ExerciseResponse;
import org.springframework.stereotype.Component;


@Component
public class ExerciseReactiveResponseBuilder extends ReactiveResponseBuilder<ExerciseResponse, ExerciseController> {
    public ExerciseReactiveResponseBuilder() {
        super(new ExerciseReactiveLinkBuilder());
    }
}
