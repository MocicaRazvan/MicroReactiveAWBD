package com.example.invoicesservice.dtos;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainingsWithExercisesResponse {

    private TrainingResponse training;
    private List<ExerciseResponse> exercises;
}
