package com.example.commonmodule.dtos.response;

import com.example.commonmodule.dtos.UserDto;
import com.example.commonmodule.hateos.CustomEntityModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Data
@AllArgsConstructor
@Schema(description = "The comment response dto")
public class ResponseWithUserLikesAndDislikesEntity<T> extends ResponseWithUserDtoEntity<T> {
    private List<UserDto> userLikes;
    private List<UserDto> userDislikes;

    public ResponseWithUserLikesAndDislikesEntity(CustomEntityModel<T> model, UserDto user, List<UserDto> userLikes, List<UserDto> userDislikes) {
        super(model, user);
        this.userLikes = userLikes;
        this.userDislikes = userDislikes;
    }

}
