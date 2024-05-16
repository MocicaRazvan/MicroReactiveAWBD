package com.example.commonmodule.dtos.generic;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@SuperBuilder
@Schema(description = "The basic dto for an entity that contains title and body")
public abstract class TitleBody {
    @NotBlank(message = "The body should not be blank.")
    @Schema(description = "The entity's body")
    private String body;

    @NotBlank(message = "The title should not be blank.")
    @Schema(description = "The entity's title")
    private String title;

    private List<String> images;
}
