package com.inghubs.asset.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inghubs.asset.dto.AssetDto;
import com.inghubs.asset.dto.CreateAssetRequestDto;
import com.inghubs.asset.dto.UpdateAssetRequestDto;
import com.inghubs.asset.model.Asset;
import com.inghubs.asset.service.AssetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;


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
    private ObjectMapper objectMapper;

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

        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

    }

    @Test
    void getAssetsByCustomerId_shouldReturnAssetsList() throws Exception {
        // Given
        List<AssetDto> assets = Arrays.asList(tryAssetDto, aselsAssetDto);
        when(assetService.getAssetsByCustomerId(customerId)).thenReturn(assets);

        // When & Then
        mockMvc.perform(get("/api/v1/assets/customer/{customerId}", customerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].customerId").value(customerId))
                .andExpect(jsonPath("$[0].assetName").value(tryAssetName))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].customerId").value(customerId))
                .andExpect(jsonPath("$[1].assetName").value(aselsAssetName));

    }

    @Test
    void getAssetsByCustomerId_ShouldReturnNoContent_WhenNoAssetsExist() throws Exception {

        Long customerId = 1L;

        when(assetService.getAssetsByCustomerId(customerId)).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/v1/assets/customer/{customerId}", customerId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
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

    @Test
    void getAssetByCustomerIdAndAssetName_ShouldReturnNotFound_WhenAssetNotExists() throws Exception {

        Long customerId = 1L;
        String assetName = "XRP";

        when(assetService.getAssetByCustomerIdAndAssetName(customerId, assetName)).thenReturn(null);

        mockMvc.perform(get("/api/v1/assets/customer/{customerId}/asset/{assetName}", customerId, assetName)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateAsset_ShouldReturnUpdatedAsset_WhenAssetExists() throws Exception {

        UpdateAssetRequestDto requestDto = new UpdateAssetRequestDto(1L, 10L,
                "TRY", BigDecimal.valueOf(100), BigDecimal.valueOf(10) );


        when(assetService.updateAsset(any(UpdateAssetRequestDto.class))).thenReturn(tryAssetDto);

        mockMvc.perform(put("/api/v1/assets/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.customerId").value(10))
                .andExpect(jsonPath("$.assetName").value("TRY"))
                .andExpect(jsonPath("$.size").value(1000))
                .andExpect(jsonPath("$.usableSize").value(300));
    }

    @Test
    void updateAsset_ShouldReturnNotFound_WhenAssetNotExists() throws Exception {

        UpdateAssetRequestDto requestDto = new UpdateAssetRequestDto(999L, 10L,
                "TRY", BigDecimal.valueOf(100), BigDecimal.valueOf(10) );

        when(assetService.updateAsset(any(UpdateAssetRequestDto.class))).thenReturn(null);

        mockMvc.perform(put("/api/v1/assets/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void createAsset_ShouldReturnCreatedAsset() throws Exception {

        CreateAssetRequestDto requestDto = new CreateAssetRequestDto(aselsAssetDto.customerId(),aselsAssetName,
                aselsAssetDto.size(), aselsAssetDto.usableSize());

        when(assetService.createAsset(any(CreateAssetRequestDto.class))).thenReturn(aselsAssetDto);

        mockMvc.perform(post("/api/v1/assets/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.customerId").value(10))
                .andExpect(jsonPath("$.assetName").value("ASELS"))
                .andExpect(jsonPath("$.size").value(100))
                .andExpect(jsonPath("$.usableSize").value(80));
    }

}
