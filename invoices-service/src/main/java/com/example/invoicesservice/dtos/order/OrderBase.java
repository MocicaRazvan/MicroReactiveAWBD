package com.example.invoicesservice.dtos.order;

import com.example.invoicesservice.dtos.generic.IdDto;
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
public class OrderBase extends IdDto {
    private String shippingAddress;

    private boolean payed = false;
}
