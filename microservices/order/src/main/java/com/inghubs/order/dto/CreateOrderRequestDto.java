package com.inghubs.order.dto;

import com.inghubs.order.model.OrderSide;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CreateOrderRequestDto(

        @NotNull(message = "Customer should be present")
        @NotEmpty(message = "Customer should be present")
        @NotBlank(message = "Customer should be present")
        Long customerId,

        @NotEmpty(message = "Asset name should be stated")
        String assetName,

        @NotNull(message = "Order side should be stated")
        OrderSide orderSide,

        @Positive(message = "Order price should be positive")
        BigDecimal price,

        @Positive(message = "Order size should be positive")
        BigDecimal size

) {
}
