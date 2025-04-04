package com.inghubs.asset.dto;

public record AssetDto(
        Long id,
        Long customerId,
        String assetName,
        int size,
        int usableSize
) {}
