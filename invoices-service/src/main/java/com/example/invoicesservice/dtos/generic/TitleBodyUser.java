package com.example.invoicesservice.dtos.generic;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
@SuperBuilder
public abstract class TitleBodyUser extends WithUser {

    private String body;

    private String title;

    private List<Long> userDislikes = new ArrayList<>();

    private List<Long> userLikes = new ArrayList<>();

    private List<String> images;
}
