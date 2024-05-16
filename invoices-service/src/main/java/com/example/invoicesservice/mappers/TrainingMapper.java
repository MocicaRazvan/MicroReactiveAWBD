package com.example.invoicesservice.mappers;

import com.example.invoicesservice.dtos.TrainingResponse;
import com.example.invoicesservice.models.Training;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TrainingMapper extends GenericMapper<Training, TrainingResponse> {

    @Override
    @Mapping(target = "exercises", ignore = true)
    Training fromResponseToModel(TrainingResponse response);

    @Override
    @Mapping(target = "exercises", ignore = true)
    TrainingResponse fromModelToResponse(Training model);
}
