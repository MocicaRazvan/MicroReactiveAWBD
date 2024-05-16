package com.example.trainingservice.hateos;


import com.example.commonmodule.hateos.controllerMaybe.ReactiveResponseBuilder;
import com.example.trainingservice.controllers.TrainingController;
import com.example.trainingservice.dto.TrainingResponse;
import org.springframework.stereotype.Component;

@Component
public class TrainingReactiveResponseBuilder extends ReactiveResponseBuilder<TrainingResponse, TrainingController> {
    public TrainingReactiveResponseBuilder() {
        super(new TrainingReactiveLinkBuilder());
    }
}
