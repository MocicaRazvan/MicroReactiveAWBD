package com.example.invoicesservice.mappers;


import com.example.invoicesservice.dtos.ExerciseResponse;
import com.example.invoicesservice.models.Exercise;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ExerciseMapper extends GenericMapper<Exercise, ExerciseResponse> {
}
