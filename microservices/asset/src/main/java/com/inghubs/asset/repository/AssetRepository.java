package com.inghubs.asset.repository;

import com.inghubs.asset.model.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssetRepository extends JpaRepository<Asset, Long> {

    Optional<Asset> findByCustomerIdAndAssetName(Long customerId, String assetName);

    List<Asset> findByCustomerId(Long customerId);

    boolean existsByCustomerIdAndAssetName(Long customerId, String assetName);
}
