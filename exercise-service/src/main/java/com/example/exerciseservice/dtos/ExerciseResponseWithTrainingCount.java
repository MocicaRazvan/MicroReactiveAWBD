package com.example.exerciseservice.dtos;

import com.example.commonmodule.dtos.UserDto;
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
public class ExerciseResponseWithTrainingCount extends ExerciseResponse {
    private Long trainingCount;
    private UserDto user;

    public ExerciseResponseWithTrainingCount fromResponse(ExerciseResponse exerciseResponse, Long trainingCount, UserDto user) {
        return ExerciseResponseWithTrainingCount.builder()
                .id(exerciseResponse.getId())
                .muscleGroups(exerciseResponse.getMuscleGroups())
                .videos(exerciseResponse.getVideos())
                .approved(exerciseResponse.isApproved())
                .title(exerciseResponse.getTitle())
                .body(exerciseResponse.getBody())
                .userLikes(exerciseResponse.getUserLikes())
                .userDislikes(exerciseResponse.getUserDislikes())
                .images(exerciseResponse.getImages())
                .userId(exerciseResponse.getUserId())
                .trainingCount(trainingCount)
                .createdAt(exerciseResponse.getCreatedAt())
                .updatedAt(exerciseResponse.getUpdatedAt())
                .user(user)
                .build();
    }

}
