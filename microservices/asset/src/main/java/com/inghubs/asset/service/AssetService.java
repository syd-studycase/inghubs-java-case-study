package com.inghubs.asset.service;

import com.inghubs.asset.dto.AssetDto;
import com.inghubs.asset.dto.CreateAssetRequestDto;
import com.inghubs.asset.dto.UpdateAssetRequestDto;
import com.inghubs.asset.exception.AssetNotFoundException;
import com.inghubs.asset.model.Asset;
import com.inghubs.asset.repository.AssetRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AssetService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

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
                .orElseThrow(() -> new AssetNotFoundException("Asset not found for customer: "
                        + customerId + " and asset name: " + assetName));
    }

    public AssetDto updateAsset(UpdateAssetRequestDto updateAssetRequestDto) {
        if (!assetRepository.existsByCustomerIdAndAssetName(updateAssetRequestDto.customerId(), updateAssetRequestDto.assetName())) {
            throw new AssetNotFoundException("Asset not found for customer: "
                    + updateAssetRequestDto.customerId()
                    + " and asset name: " + updateAssetRequestDto.assetName());
        }

        Asset asset = assetMapper.toAsset(updateAssetRequestDto);
        Asset updatedAsset = assetRepository.save(asset);
        logger.debug("Asset updated with ID: {}", updatedAsset.getId());

        return assetMapper.fromAsset(updatedAsset);
    }

    public AssetDto createAsset(CreateAssetRequestDto createAssetRequestDto) {
        Asset asset = assetMapper.toAsset(createAssetRequestDto);
        Asset savedAsset = assetRepository.save(asset);
        logger.debug("Asset saved with ID: {}", savedAsset.getId());

        return assetMapper.fromAsset(savedAsset);
    }
}
