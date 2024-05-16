package com.example.trainingservice.dto;

import com.example.commonmodule.dtos.generic.TitleBody;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
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
@Schema(description = "The training request dto")
public class TrainingBody extends TitleBody {
    @NotNull(message = "The price should be present")
    @Min(value = 1, message = "The price should be at least 1.")
    @Schema(description = "The price of the training", minimum = "1")
    private double price;

    @NotEmpty(message = "The exercises should not be empty.")
    @NotNull(message = "The exercises should not be null.")
    @Schema(description = "The exercises id's contained in the training, the length should be at least 1.")
    private List<Long> exercises;

}
