package com.inghubs.asset.controller;

import com.inghubs.asset.dto.AssetDto;
import com.inghubs.asset.model.Asset;
import com.inghubs.asset.service.AssetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@ActiveProfiles("dev")
@WebMvcTest(AssetController.class)
public class AssetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
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
    void getAssetsByCustomerId_shouldReturnAssetsList() throws Exception {
        // Given
        List<AssetDto> assets = Arrays.asList(tryAssetDto, aselsAssetDto);
        when(assetService.getAssetsByCustomerId(customerId)).thenReturn(assets);


        // When & Then
        mockMvc.perform(get("/api/v1/assets/customer/{customerId}", customerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].customerId").value(customerId))
                .andExpect(jsonPath("$[0].assetName").value(tryAssetName))
                .andExpect(jsonPath("$[1].assetName").value(aselsAssetName));
    }

    @Test
    void getAssetByCustomerIdAndAssetName_shouldReturnAsset() throws Exception {
        // Given
        when(assetService.getAssetByCustomerIdAndAssetName(customerId, tryAssetName)).thenReturn(tryAssetDto);

        // When & Then
        mockMvc.perform(get("/api/v1/assets/customer/{customerId}/asset/{assetName}", customerId, tryAssetName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value(customerId))
                .andExpect(jsonPath("$.assetName").value(tryAssetName))
                .andExpect(jsonPath("$.size").value("1000"))
                .andExpect(jsonPath("$.usableSize").value("300"));
    }

}
