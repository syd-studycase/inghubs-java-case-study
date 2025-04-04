package com.inghubs.order.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.math.BigDecimal;

@FeignClient(name="asset")
public interface AssetServiceClient {

    @GetMapping(value = "/api/v1/assets/check-balance")
    boolean checkBalance(Long customerId, String assetName, BigDecimal requiredAmount);

    @GetMapping(value = "/api/v1/assets/check-usable-asset-size")
    boolean checkUsableAssetSize(String assetName, int size);

    @PostMapping(value = "/api/v1/assets/refund")
    void refund(Long customerId, String assetName, int amount);
}
