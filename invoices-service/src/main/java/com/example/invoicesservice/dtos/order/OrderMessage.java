package com.example.invoicesservice.dtos.order;

import com.example.invoicesservice.dtos.generic.IdDto;
import com.example.invoicesservice.enums.MessageType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
@SuperBuilder
public class OrderMessage extends OrderBase {


    private MessageType messageType;

    private Long userId;


    private List<Long> trainings;

}
