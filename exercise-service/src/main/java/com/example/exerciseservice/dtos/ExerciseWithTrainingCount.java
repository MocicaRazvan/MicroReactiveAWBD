package com.example.exerciseservice.dtos;

import com.example.exerciseservice.models.Exercise;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseWithTrainingCount extends Exercise {
    private Long trainingCount;
}
