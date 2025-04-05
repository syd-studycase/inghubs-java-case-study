package com.inghubs.asset.service;

import com.inghubs.asset.dto.AssetDto;
import com.inghubs.asset.exception.AssetNotFoundException;
import com.inghubs.asset.repository.AssetRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AssetService {

    private final AssetRepository assetRepository;
    private final AssetMapper assetMapper;

    public AssetService(AssetRepository assetRepository, AssetMapper assetMapper) {
        this.assetRepository = assetRepository;
        this.assetMapper = assetMapper;
    }

    public List<AssetDto> getAssetsByCustomerId(Long customerId) {
        return assetRepository.findByCustomerId(customerId)
                .stream()
                .map(this.assetMapper::fromAsset)
                .collect(Collectors.toList());
    }

    public AssetDto getAssetByCustomerIdAndAssetName(Long customerId, String assetName) {
        return assetRepository.findByCustomerIdAndAssetName(customerId, assetName)
                .map(this.assetMapper::fromAsset)
                .orElseThrow(() -> new AssetNotFoundException("Asset not found for customer: " + customerId + " and asset name: " + assetName));
    }
}
