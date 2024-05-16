package com.example.orderservice.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Schema(description = "The dto used to 'pay' an order")
public class PriceDto {

    @NotNull(message = "The price should be present")
    @Min(value = 1, message = "The price should be at least 1.")
    @Schema(description = "The price provided in the request", minimum = "1")
    private double price;

}
