package com.inghubs.asset.service;

import com.inghubs.asset.dto.AssetDto;
import com.inghubs.asset.model.Asset;
import org.springframework.stereotype.Service;

@Service
public class AssetMapper {

    public Asset toAsset(AssetDto assetDto) {

        if (assetDto == null) {
            return null;
        }

        return new Asset(
                assetDto.customerId(),
                assetDto.assetName(),
                assetDto.size(),
                assetDto.usableSize());
    }

    public AssetDto fromAsset(Asset asset) {
        return new AssetDto(
                asset.getId(),
                asset.getCustomerId(),
                asset.getAssetName(),
                asset.getSize(),
                asset.getUsableSize()
        );
    }
}
