package com.example.commonmodule.dtos.generic;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "The basic dto for an entity that contains a user")
public abstract class WithUser extends IdDto {


    @Schema(description = "The owner's id")
    private Long userId;
}
