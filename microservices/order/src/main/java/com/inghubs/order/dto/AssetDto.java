package com.inghubs.order.dto;

import java.math.BigDecimal;

public record AssetDto(
        Long id,
        Long customerId,
        String assetName,
        BigDecimal size,
        BigDecimal usableSize
) {}
