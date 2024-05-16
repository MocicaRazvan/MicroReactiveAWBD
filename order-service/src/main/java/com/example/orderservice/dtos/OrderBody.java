package com.example.orderservice.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@SuperBuilder
@Schema(description = "The order request dto")
public class OrderBody {

    @NotBlank(message = "The shipping address should not be empty.")
    @Schema(description = "The order's shipping address")
    private String shippingAddress;

    @NotNull(message = "Payed cannot be null.")
    @Schema(minimum = "The order payed status", defaultValue = "false")
    private boolean payed = false;

    @NotEmpty(message = "The trainings should not be empty.")
    @NotNull(message = "The trainings should not be null.")
    @Schema(description = "The training id's contained in the order, the length should be at least 1.")
    private List<Long> trainings;
}
