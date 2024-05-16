package com.example.invoicesservice.dtos;

import com.example.invoicesservice.dtos.generic.Approve;
import com.example.invoicesservice.utils.Transformable;
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
public class TrainingResponse extends Approve implements Transformable<TrainingResponse> {
    private double price;

    private List<Long> exercises;
}
