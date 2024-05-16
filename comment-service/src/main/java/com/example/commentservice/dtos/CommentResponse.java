package com.example.commentservice.dtos;


import com.example.commonmodule.dtos.generic.TitleBodyUser;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Data
@SuperBuilder
@Schema(description = "The comment response dto")
public class CommentResponse extends TitleBodyUser {

    @Schema(description = "The post's id for which the comment belongs")
    private Long postId;
}
