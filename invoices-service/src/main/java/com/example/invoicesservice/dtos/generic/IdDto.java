package com.example.invoicesservice.dtos.generic;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Data
public abstract class IdDto {
    private Long id;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
