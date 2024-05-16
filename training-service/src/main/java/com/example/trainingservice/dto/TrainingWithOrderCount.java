package com.example.trainingservice.dto;

import com.example.trainingservice.models.Training;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class TrainingWithOrderCount extends Training {
    private Long orderCount;
}
