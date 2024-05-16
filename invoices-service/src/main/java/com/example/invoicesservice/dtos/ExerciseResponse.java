package com.example.invoicesservice.dtos;

import com.example.invoicesservice.dtos.generic.Approve;
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
public class ExerciseResponse extends Approve {
    private List<String> muscleGroups;
    private List<String> videos;
}
