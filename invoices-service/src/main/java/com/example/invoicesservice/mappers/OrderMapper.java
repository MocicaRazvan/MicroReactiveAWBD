package com.example.invoicesservice.mappers;

import com.example.invoicesservice.dtos.TrainingsWithExercisesResponse;
import com.example.invoicesservice.dtos.order.OrderDto;
import com.example.invoicesservice.dtos.order.OrderMessage;
import com.example.invoicesservice.models.Exercise;
import com.example.invoicesservice.models.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class OrderMapper {


    @Autowired
    private TrainingMapper trainingMapper;
    @Autowired
    private ExerciseMapper exerciseMapper;

    @Mapping(target = "trainings", ignore = true)
    public abstract Order fromMessageToModel(OrderMessage message);

    @Mapping(target = "trainings", ignore = true)
    public abstract OrderMessage fromModelToMessage(Order model);

    @Mapping(target = "trainings", ignore = true)
    public abstract OrderDto fromModelToResponse(Order model);

    @Mapping(target = "trainings", ignore = true)
    public abstract Order fromDtoToModel(OrderDto dto);

    public OrderDto fromModelToDtoWithTrainings(Order model) {
        OrderDto orderDto = fromModelToResponse(model);

        orderDto.setTrainings(model.getTrainings().stream().map(
                te -> TrainingsWithExercisesResponse.builder()
                        .training(trainingMapper.fromModelToResponse(te).map(t -> {
                            t.setExercises(te.getExercises().stream().map(Exercise::getId).toList());
                            return t;
                        }))
                        .exercises(te.getExercises().stream().map(exerciseMapper::fromModelToResponse).toList())
                        .build()
        ).toList());
        return orderDto;
    }

}
