package com.inghubs.asset.dto;

import java.math.BigDecimal;

public record CreateAssetRequestDto(
        Long customerId,
        String assetName,
        BigDecimal size,
        BigDecimal usableSize
) {

}
