package com.inghubs.asset.service;

import com.inghubs.asset.dto.AssetDto;
import com.inghubs.asset.exception.AssetNotFoundException;
import com.inghubs.asset.model.Asset;
import com.inghubs.asset.repository.AssetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AssetServiceTest {

    @Mock
    private AssetRepository assetRepository;

    @Mock
    private AssetMapper assetMapper;

    @InjectMocks
    private AssetService assetService;


    private Asset tryAsset;
    private Asset aselsAsset;
    private AssetDto tryAssetDto;
    private AssetDto aselsAssetDto;
    private final Long customerId = 10L;
    private final String tryAssetName = "TRY";
    private final String aselsAssetName = "ASELS";

    @BeforeEach
    void setUp() {
        tryAsset = new Asset(
                customerId,
                tryAssetName,
                BigDecimal.valueOf(1000),
                BigDecimal.valueOf(300)
        );

        tryAsset.setId(1L);

        tryAssetDto = new AssetDto(
                tryAsset.getId(),
                customerId,
                tryAssetName,
                BigDecimal.valueOf(1000),
                BigDecimal.valueOf(300)
        );

        aselsAsset = new Asset(
                customerId,
                aselsAssetName,
                BigDecimal.valueOf(100),
                BigDecimal.valueOf(80)
        );

        aselsAsset.setId(2L);

        aselsAssetDto = new AssetDto(
                aselsAsset.getId(),
                customerId,
                aselsAssetName,
                BigDecimal.valueOf(100),
                BigDecimal.valueOf(80)
        );


    }

    @Test
    void getAssetsByCustomerId_shouldReturnAssets() {
        // Given
        when(assetRepository.findByCustomerId(customerId)).thenReturn(Arrays.asList(tryAsset, aselsAsset));
        when(assetMapper.fromAsset(tryAsset)).thenReturn(tryAssetDto);
        when(assetMapper.fromAsset(aselsAsset)).thenReturn(aselsAssetDto);

        // When
        List<AssetDto> assets = assetService.getAssetsByCustomerId(customerId);

        // Then
        assertEquals(2, assets.size());
        assertEquals(tryAssetName, assets.get(0).assetName());
        assertEquals(aselsAssetName, assets.get(1).assetName());
    }

    @Test
    void getAssetByCustomerIdAndAssetName_shouldReturnAsset() {
        // Given
        when(assetRepository.findByCustomerIdAndAssetName(customerId, tryAssetName)).thenReturn(Optional.of(tryAsset));
        when(assetMapper.fromAsset(tryAsset)).thenReturn(tryAssetDto);

        // When
        AssetDto assetDto = assetService.getAssetByCustomerIdAndAssetName(customerId, tryAssetName);

        // Then
        assertEquals(tryAssetName, assetDto.assetName());
        assertEquals(customerId, assetDto.customerId());
        assertEquals(BigDecimal.valueOf(1000), assetDto.size());
    }

    @Test
    void getAssetByCustomerIdAndAssetName_shouldThrowExceptionWhenNotFound() {
        // Given
        when(assetRepository.findByCustomerIdAndAssetName(customerId, "RANDOM")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(AssetNotFoundException.class, () ->
                assetService.getAssetByCustomerIdAndAssetName(customerId, "RANDOM")
        );
    }

}
