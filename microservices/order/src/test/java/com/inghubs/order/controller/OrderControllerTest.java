package com.inghubs.order.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.inghubs.order.dto.AssetDto;
import com.inghubs.order.dto.CreateOrderRequestDto;
import com.inghubs.order.dto.OrderResponseDto;
import com.inghubs.order.model.Order;
import com.inghubs.order.model.OrderSide;
import com.inghubs.order.model.OrderStatus;
import com.inghubs.order.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("dev")
@WebMvcTest(OrderController.class)
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    private CreateOrderRequestDto buyOrderRequest;
    private CreateOrderRequestDto sellOrderRequest;
    private AssetDto tryAsset;
    private AssetDto googlAsset;
    private Order pendingOrder;
    private OrderResponseDto pendingBuyOrderResponseDto;
    private OrderResponseDto pendingSellOrderResponseDto;
    LocalDateTime endDate;
    LocalDateTime startDate;
    ObjectMapper objectMapper;
    Long customerId;

    @BeforeEach
    void setUp() {
        customerId = 10L;
        String assetName = "GOOGL";

        buyOrderRequest = new CreateOrderRequestDto(customerId, assetName, OrderSide.BUY,
                BigDecimal.valueOf(10), BigDecimal.valueOf(150));


        sellOrderRequest = new CreateOrderRequestDto(customerId, assetName, OrderSide.SELL,
                BigDecimal.valueOf(5), BigDecimal.valueOf(150));

        tryAsset = new AssetDto(1L, customerId, "TRY", BigDecimal.valueOf(5000),
                BigDecimal.valueOf(5000));


        googlAsset = new AssetDto(2L, customerId, assetName, BigDecimal.valueOf(200),
                BigDecimal.valueOf(200));

        pendingOrder = new Order(customerId, assetName, OrderSide.BUY,
                BigDecimal.valueOf(10), BigDecimal.valueOf(150), OrderStatus.PENDING, LocalDateTime.now());
        pendingOrder.setId(1L);

        endDate = LocalDateTime.now();
        startDate = endDate.minusDays(7);

        pendingBuyOrderResponseDto = new OrderResponseDto(pendingOrder.getId(), customerId, assetName, OrderSide.BUY, BigDecimal.valueOf(10),
                BigDecimal.valueOf(150), OrderStatus.PENDING, LocalDateTime.now());

        pendingSellOrderResponseDto = new OrderResponseDto(2L, customerId, assetName, OrderSide.SELL, BigDecimal.valueOf(10),
                BigDecimal.valueOf(150), OrderStatus.PENDING, LocalDateTime.now());

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

    }

    @Test
    void createOrder_ShouldReturnCreatedOrder() throws Exception {

        when(orderService.createOrder(any(CreateOrderRequestDto.class))).thenReturn(pendingBuyOrderResponseDto);

        // Act & Assert
        mockMvc.perform(post("/api/v1/orders/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buyOrderRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.customerId").value(10))
                .andExpect(jsonPath("$.assetName").value("GOOGL"))
                .andExpect(jsonPath("$.orderSide").value("BUY"))
                .andExpect(jsonPath("$.price").value(10))
                .andExpect(jsonPath("$.size").value(150));

        verify(orderService, times(1)).createOrder(any(CreateOrderRequestDto.class));
    }

    @Test
    void listOrders_ShouldReturnOrderList_WhenOrdersExist() throws Exception {

        List<OrderResponseDto> orderList = Arrays.asList(pendingBuyOrderResponseDto, pendingSellOrderResponseDto);

        when(orderService.listOrders(eq(pendingBuyOrderResponseDto.customerId()), eq(startDate), eq(endDate))).thenReturn(orderList);

        mockMvc.perform(get("/api/v1/orders")
                        .param("customerId", customerId.toString())
                        .param("start", startDate.toString())
                        .param("end", endDate.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].customerId").value(customerId))
                .andExpect(jsonPath("$[0].assetName").value("GOOGL"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].customerId").value(customerId))
                .andExpect(jsonPath("$[1].assetName").value("GOOGL"));
    }

    @Test
    void listOrders_ShouldReturnNoContent_WhenNoOrdersExist() throws Exception {

        LocalDateTime start = LocalDateTime.of(2023, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2023, 12, 31, 23, 59);
        Long customerId = 1L;

        when(orderService.listOrders(eq(customerId), eq(start), eq(end))).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/v1/orders")
                        .param("customerId", customerId.toString())
                        .param("start", start.toString())
                        .param("end", end.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void cancelOrder_ShouldReturnNoContent_WhenOrderCancelled() throws Exception {
        Long orderId = 1L;
        when(orderService.cancelOrder(orderId)).thenReturn(true);

        mockMvc.perform(delete("/api/v1/orders/{orderId}", orderId))
                .andExpect(status().isNoContent());
    }

    @Test
    void cancelOrder_ShouldReturnNotFound_WhenOrderNotExists() throws Exception {
        Long orderId = 999L;
        when(orderService.cancelOrder(orderId)).thenReturn(false);

        mockMvc.perform(delete("/api/v1/orders/{orderId}", orderId))
                .andExpect(status().isNotFound());
    }

}
