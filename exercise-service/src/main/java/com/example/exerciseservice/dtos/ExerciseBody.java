package com.example.exerciseservice.dtos;

import com.example.commonmodule.dtos.generic.TitleBody;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
@SuperBuilder
@Schema(description = "The exercise request object")
public class ExerciseBody extends TitleBody {

    @NotEmpty(message = "The muscle groups should not be empty.")
    @NotNull(message = "The muscle groups should not be null.")
    @Schema(description = "The exercises muscle groups, the length should be at least one")
    private List<String> muscleGroups;

    private List<String> videos;

}
