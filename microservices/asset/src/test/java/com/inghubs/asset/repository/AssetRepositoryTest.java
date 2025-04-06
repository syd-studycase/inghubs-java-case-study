package com.inghubs.asset.repository;

import com.inghubs.asset.model.Asset;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("dev")
@DataJpaTest
public class AssetRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AssetRepository assetRepository;

    @Test
    void findByCustomerIdAndAssetName_ShouldReturnAsset() {

        Asset asset = new Asset();
        asset.setCustomerId(1L);
        asset.setAssetName("BTC");
        asset.setSize(new BigDecimal("500.0"));
        asset.setUsableSize(new BigDecimal("200.0"));

        entityManager.persist(asset);
        entityManager.flush();

        Optional<Asset> found = assetRepository.findByCustomerIdAndAssetName(1L, "BTC");

        assertTrue(found.isPresent());
        assertEquals("BTC", found.get().getAssetName());
        assertEquals(1L, found.get().getCustomerId());
        assertEquals(0, new BigDecimal("500.0").compareTo(found.get().getSize()));
        assertEquals(0, new BigDecimal("200.0").compareTo(found.get().getUsableSize()));

    }

    @Test
    void findByCustomerIdAndAssetName_ShouldReturnEmpty_WhenAssetDoesNotExist() {

        Optional<Asset> found = assetRepository.findByCustomerIdAndAssetName(999L, "NOTEXIST");

        assertFalse(found.isPresent());
    }

    @Test
    void findByCustomerId_ShouldReturnAllCustomerAssets() {

        Asset asset1 = new Asset();
        asset1.setCustomerId(2L);
        asset1.setAssetName("TRY");
        asset1.setSize(new BigDecimal("2000.0"));
        asset1.setUsableSize(new BigDecimal("300.0"));

        Asset asset2 = new Asset();
        asset2.setCustomerId(2L);
        asset2.setAssetName("ASELS");
        asset2.setSize(new BigDecimal("100.0"));
        asset2.setUsableSize(new BigDecimal("10.00"));

        entityManager.persist(asset1);
        entityManager.persist(asset2);
        entityManager.flush();

        List<Asset> foundAssets = assetRepository.findByCustomerId(2L);

        assertThat(foundAssets).hasSize(2);
        assertThat(foundAssets).extracting(Asset::getAssetName).containsExactlyInAnyOrder("TRY", "ASELS");
    }

    @Test
    void findByCustomerId_ShouldReturnEmptyList_WhenNoAssetsExist() {

        List<Asset> foundAssets = assetRepository.findByCustomerId(999L);

        assertThat(foundAssets).isEmpty();
    }

    @Test
    void save_ShouldPersistAsset_WhenAssetIsValid() {

        Asset asset = new Asset();
        asset.setCustomerId(2L);
        asset.setAssetName("TRY");
        asset.setSize(new BigDecimal("2000.0"));
        asset.setUsableSize(new BigDecimal("300.0"));

        Asset savedAsset = assetRepository.save(asset);

        assertNotNull(savedAsset.getId());

        Asset retrievedAsset = entityManager.find(Asset.class, savedAsset.getId());
        assertNotNull(retrievedAsset);
        assertEquals("TRY", retrievedAsset.getAssetName());
        assertEquals(2L, retrievedAsset.getCustomerId());
        assertEquals(0, new BigDecimal("2000.0").compareTo(retrievedAsset.getSize()));
        assertEquals(0, new BigDecimal("300.0").compareTo(retrievedAsset.getUsableSize()));
    }

    @Test
    void existsByCustomerIdAndAssetName_ShouldReturnTrue() {

        Asset asset = new Asset();
        asset.setCustomerId(2L);
        asset.setAssetName("TRY");
        asset.setSize(new BigDecimal("2000.0"));
        asset.setUsableSize(new BigDecimal("300.0"));

        entityManager.persist(asset);
        entityManager.flush();

        assertTrue(assetRepository.existsByCustomerIdAndAssetName(2L, "TRY"));

    }

    @Test
    void existsByCustomerIdAndAssetName_ShouldReturnFalse_WhenAssetDoesNotExist() {

        assertFalse(assetRepository.existsByCustomerIdAndAssetName(999L, "NOTEXIST"));
    }

}
