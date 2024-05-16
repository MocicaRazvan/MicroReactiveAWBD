package com.example.orderservice.dtos;

import com.example.commonmodule.dtos.generic.WithUser;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
@SuperBuilder
@Schema(description = "The basic order structure dto")
public class OrderStructure extends WithUser {
    @Schema(description = "The order's shipping address")
    private String shippingAddress;

    @Schema(minimum = "The order payed status", defaultValue = "false")
    private boolean payed = false;
}
