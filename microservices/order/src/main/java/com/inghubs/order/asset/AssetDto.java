package com.inghubs.order.asset;

import java.math.BigDecimal;

public record AssetDto(
        Long id,
        Long customerId,
        String assetName,
        BigDecimal size,
        BigDecimal usableSize
) {}
