package com.inghubs.asset.dto;

import java.math.BigDecimal;

public record UpdateAssetRequestDto(
        Long id,
        Long customerId,
        String assetName,
        BigDecimal size,
        BigDecimal usableSize
) {
}
