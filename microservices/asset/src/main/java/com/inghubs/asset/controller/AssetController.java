package com.inghubs.asset.controller;

import com.inghubs.asset.dto.AssetDto;
import com.inghubs.asset.dto.CreateAssetRequestDto;
import com.inghubs.asset.dto.UpdateAssetRequestDto;
import com.inghubs.asset.service.AssetService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/assets")
public class AssetController {

    private final AssetService assetService;

    public AssetController(AssetService assetService) {
        this.assetService = assetService;
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<AssetDto>> getAssetsByCustomerId(
            @PathVariable Long customerId) {
        return ResponseEntity.ok(assetService.getAssetsByCustomerId(customerId));
    }

    @GetMapping("/customer/{customerId}/asset/{assetName}")
    public ResponseEntity<AssetDto> getAssetByCustomerIdAndAssetName(
            @PathVariable Long customerId,
            @PathVariable String assetName) {
        return ResponseEntity.ok(assetService.getAssetByCustomerIdAndAssetName(customerId, assetName));
    }

    @PutMapping("/update")
    public ResponseEntity<AssetDto> updateAsset(@RequestBody UpdateAssetRequestDto updateAssetRequestDto) {
        return ResponseEntity.ok(assetService.updateAsset(updateAssetRequestDto));
    }

    @PostMapping("/create")
    public ResponseEntity<AssetDto> createAsset(@RequestBody CreateAssetRequestDto createAssetRequestDto) {
        return ResponseEntity.ok(assetService.createAsset(createAssetRequestDto));
    }

}
