package com.example.trainingservice.dto.exercises;

import com.example.commonmodule.dtos.generic.Approve;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Schema(description = "The exercise response dto")
public class ExerciseResponse extends Approve {
    @Schema(description = "The exercise's muscle groups")
    private List<String> muscleGroups;
    private List<String> videos;
}
