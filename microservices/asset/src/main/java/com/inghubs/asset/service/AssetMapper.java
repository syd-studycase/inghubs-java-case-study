package com.inghubs.asset.service;

import com.inghubs.asset.dto.AssetDto;
import com.inghubs.asset.dto.CreateAssetRequestDto;
import com.inghubs.asset.dto.UpdateAssetRequestDto;
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

    public Asset toAsset(UpdateAssetRequestDto updateAssetRequestDto) {

        if (updateAssetRequestDto == null) {
            return null;
        }

        Asset asset = new Asset(
                updateAssetRequestDto.customerId(),
                updateAssetRequestDto.assetName(),
                updateAssetRequestDto.size(),
                updateAssetRequestDto.usableSize());

        asset.setId(updateAssetRequestDto.id());
        return asset;
    }

    public Asset toAsset(CreateAssetRequestDto createAssetRequestDto) {

        if (createAssetRequestDto == null) {
            return null;
        }

        return new Asset(
                createAssetRequestDto.customerId(),
                createAssetRequestDto.assetName(),
                createAssetRequestDto.size(),
                createAssetRequestDto.usableSize());

    }
}
