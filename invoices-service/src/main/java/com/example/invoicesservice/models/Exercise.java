package com.example.invoicesservice.models;

import com.example.invoicesservice.convertors.ListToStringConverter;
import com.example.invoicesservice.models.generics.Approve;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
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
@Entity
public class Exercise extends Approve {
    @Convert(converter = ListToStringConverter.class)
    private List<String> muscleGroups;

    @Convert(converter = ListToStringConverter.class)
    private List<String> videos;
}
