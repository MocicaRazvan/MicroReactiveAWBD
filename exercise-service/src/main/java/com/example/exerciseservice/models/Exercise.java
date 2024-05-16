package com.example.exerciseservice.models;

import com.example.commonmodule.models.Approve;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Table("exercise")
public class Exercise extends Approve {
    @Column("muscle_groups")
    private List<String> muscleGroups;

    private List<String> videos;
}
