package com.example.exerciseservice.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
@SuperBuilder
@Schema(description = "The exercise body dto that also contains the id")
public class ExerciseBodyWithId extends ExerciseBody {
    @NotNull(message = "The id should not be null.")
    @Schema(description = "The exercise's id")
    private Long id;
}
