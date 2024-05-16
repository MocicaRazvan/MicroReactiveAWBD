package com.example.trainingservice.hateos;


import com.example.commonmodule.hateos.controllerMaybe.generics.ApproveReactiveLinkBuilder;
import com.example.trainingservice.controllers.TrainingController;
import com.example.trainingservice.dto.TrainingBody;
import com.example.trainingservice.dto.TrainingResponse;
import com.example.trainingservice.mappers.TrainingMapper;
import com.example.trainingservice.models.Training;
import com.example.trainingservice.repositories.TrainingRepository;
import com.example.trainingservice.services.TrainingService;
import org.springframework.hateoas.server.reactive.WebFluxLinkBuilder;

import java.util.List;

public class TrainingReactiveLinkBuilder extends ApproveReactiveLinkBuilder<Training, TrainingBody,
        TrainingResponse, TrainingRepository, TrainingMapper, TrainingService, TrainingController> {

    @Override
    public List<WebFluxLinkBuilder.WebFluxLink> createModelLinks(TrainingResponse trainingResponse, Class<TrainingController> c) {
        List<WebFluxLinkBuilder.WebFluxLink> links = super.createModelLinks(trainingResponse, c);
        links.add(WebFluxLinkBuilder.linkTo(WebFluxLinkBuilder.methodOn(c).getTrainingWithExercises(trainingResponse.getId())).withRel("getWithExercises"));
        links.add(WebFluxLinkBuilder.linkTo(WebFluxLinkBuilder.methodOn(c).getTrainingsWithExercises(List.of(1L, 2L))).withRel("getApprovedWithExercises"));
        return links;
    }

}
