package com.example.trainingservice.dto;


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
public class TrainingResponseWithOrderCount extends TrainingResponse {
    private Long orderCount;
    private UserDto user;


    public TrainingResponseWithOrderCount fromTrainingResponse(TrainingResponse trainingResponse, Long orderCount, UserDto user) {
        return TrainingResponseWithOrderCount.builder()
                .id(trainingResponse.getId())
                .price(trainingResponse.getPrice())
                .exercises(trainingResponse.getExercises())
                .approved(trainingResponse.isApproved())
                .body(trainingResponse.getBody())
                .title(trainingResponse.getTitle())
                .userLikes(trainingResponse.getUserLikes())
                .userDislikes(trainingResponse.getUserDislikes())
                .images(trainingResponse.getImages())
                .userId(trainingResponse.getUserId())
                .createdAt(trainingResponse.getCreatedAt())
                .updatedAt(trainingResponse.getUpdatedAt())
                .orderCount(orderCount)
                .user(user)
                .build();
    }
}
