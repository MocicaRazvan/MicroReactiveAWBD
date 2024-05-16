package com.example.commentservice.dtos;

import com.example.commonmodule.dtos.generic.TitleBody;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Data
@SuperBuilder
public class CommentBody extends TitleBody {
}
