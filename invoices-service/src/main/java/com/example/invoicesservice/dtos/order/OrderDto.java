package com.example.invoicesservice.dtos.order;

import com.example.invoicesservice.dtos.TrainingResponse;
import com.example.invoicesservice.dtos.TrainingsWithExercisesResponse;
import com.example.invoicesservice.dtos.UserDto;
import com.example.invoicesservice.utils.Transformable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class OrderDto extends OrderBase implements Transformable<OrderDto> {
    private List<TrainingsWithExercisesResponse> trainings;
    private UserDto user;
}
