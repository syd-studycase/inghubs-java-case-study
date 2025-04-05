package com.inghubs.order.dto;

import com.inghubs.order.model.OrderSide;
import com.inghubs.order.model.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderResponseDto(
        Long id,
        Long customerId,
        String assetName,
        OrderSide orderSide,
        BigDecimal price,
        BigDecimal size,
        OrderStatus orderStatus,
        LocalDateTime createDate
) {
}
