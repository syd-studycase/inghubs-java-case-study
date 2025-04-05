package com.inghubs.order.proxy;

import com.inghubs.order.dto.AssetDto;
import com.inghubs.order.dto.CreateAssetRequestDto;
import com.inghubs.order.dto.UpdateAssetRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@FeignClient(name="asset")
public interface AssetServiceClient {

    @GetMapping("/api/v1/assets/customer/{customerId}/asset/{assetName}")
    Optional<AssetDto> getAssetByCustomerIdAndAssetName(@PathVariable String customerId, @PathVariable String assetName);

    @PutMapping("/api/v1/assets/update")
    Optional<AssetDto> updateAsset(@RequestBody UpdateAssetRequestDto updateAssetRequestDto);

    @PostMapping("/api/v1/assets/create")
    Optional<AssetDto> createAsset(@RequestBody CreateAssetRequestDto createAssetRequestDto);

//    @GetMapping(value = "/api/v1/assets/check-balance/{customer}")
//    boolean checkBalance(@PathVariable Long customerId, String assetName, BigDecimal requiredAmount);
//
//    @GetMapping(value = "/api/v1/assets/check-usable-asset-size")
//    boolean checkUsableAssetSize(String assetName, int size);
//
//    @PostMapping(value = "/api/v1/assets/refund")
//    void refund(Long customerId, String assetName, int amount);
}
