package com.inghubs.asset.controller;

import com.inghubs.asset.dto.AssetDto;
import com.inghubs.asset.dto.CreateAssetRequestDto;
import com.inghubs.asset.dto.UpdateAssetRequestDto;
import com.inghubs.asset.service.AssetService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/assets")
public class AssetController {

    private final AssetService assetService;

    public AssetController(AssetService assetService) {
        this.assetService = assetService;
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<AssetDto>> getAssetsByCustomerId(@PathVariable Long customerId) {
        List<AssetDto> assets = assetService.getAssetsByCustomerId(customerId);
        if (assets.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 No Content
        }
        return ResponseEntity.ok(assets); // 200 OK
    }

    @GetMapping("/customer/{customerId}/asset/{assetName}")
    public ResponseEntity<AssetDto> getAssetByCustomerIdAndAssetName(
            @PathVariable String customerId,
            @PathVariable String assetName) {
        AssetDto asset = assetService.getAssetByCustomerIdAndAssetName(Long.valueOf(customerId), assetName);
        if (asset != null) {
            return ResponseEntity.ok(asset); // 200 OK
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 404 Not Found
        }
    }

    @PutMapping("/update")
    public ResponseEntity<AssetDto> updateAsset(@RequestBody UpdateAssetRequestDto updateAssetRequestDto) {
        AssetDto updatedAsset = assetService.updateAsset(updateAssetRequestDto);
        if (updatedAsset != null) {
            return ResponseEntity.ok(updatedAsset); // 200 OK
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 404 Not Found
        }
    }

    @PostMapping("/create")
    public ResponseEntity<AssetDto> createAsset(@RequestBody CreateAssetRequestDto createAssetRequestDto) {
        AssetDto createdAsset = assetService.createAsset(createAssetRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAsset); // 201 Created
    }
}
