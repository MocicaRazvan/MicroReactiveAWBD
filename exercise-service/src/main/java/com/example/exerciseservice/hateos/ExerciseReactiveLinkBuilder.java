package com.example.exerciseservice.hateos;


import com.example.commonmodule.hateos.controllerMaybe.generics.ApproveReactiveLinkBuilder;
import com.example.exerciseservice.controllers.ExerciseController;
import com.example.exerciseservice.dtos.ExerciseBody;
import com.example.exerciseservice.dtos.ExerciseResponse;
import com.example.exerciseservice.mappers.ExerciseMapper;
import com.example.exerciseservice.models.Exercise;
import com.example.exerciseservice.repositories.ExerciseRepository;
import com.example.exerciseservice.services.ExerciseService;

public class ExerciseReactiveLinkBuilder extends ApproveReactiveLinkBuilder<Exercise, ExerciseBody, ExerciseResponse,
        ExerciseRepository, ExerciseMapper, ExerciseService, ExerciseController> {


}
