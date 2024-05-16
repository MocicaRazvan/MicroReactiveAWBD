package com.example.invoicesservice.models.generics;

import com.example.invoicesservice.convertors.ListToLongConverter;
import com.example.invoicesservice.convertors.ListToStringConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@MappedSuperclass
public abstract class TitleBody extends ManyToOneUser {
    private String body;
    private String title;

    @Convert(converter = ListToStringConverter.class)
    @Column(name = "images")
    private List<String> images;

    @Convert(converter = ListToLongConverter.class)
    @Column(name = "user_likes")
    private List<Long> userLikes;

    @Convert(converter = ListToLongConverter.class)
    @Column(name = "user_dislikes")
    private List<Long> userDislikes;
}
