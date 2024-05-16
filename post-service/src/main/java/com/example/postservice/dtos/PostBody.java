package com.example.postservice.dtos;


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
@Schema(description = "The post request dto")
public class PostBody extends TitleBody {
    @NotEmpty(message = "The tags should not be empty.")
    @NotNull(message = "The tags should not be null.")
    @Schema(description = "The tags contained in the post, the length should be at least 1.")
    private List<String> tags;

}
