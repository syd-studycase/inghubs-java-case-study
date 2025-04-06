package com.inghubs.order.service;

import com.inghubs.order.dto.AssetDto;
import com.inghubs.order.dto.CreateOrderRequestDto;
import com.inghubs.order.dto.OrderResponseDto;
import com.inghubs.order.exception.InsufficientBalanceException;
import com.inghubs.order.exception.OrderNotFoundException;
import com.inghubs.order.model.Order;
import com.inghubs.order.model.OrderSide;
import com.inghubs.order.model.OrderStatus;
import com.inghubs.order.proxy.AssetServiceClient;
import com.inghubs.order.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private AssetServiceClient assetServiceClient;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
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

    @BeforeEach
    void setUp() {
        Long customerId = 10L;
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

        pendingSellOrderResponseDto = new OrderResponseDto(pendingOrder.getId(), customerId, assetName, OrderSide.SELL, BigDecimal.valueOf(10),
                BigDecimal.valueOf(150), OrderStatus.PENDING, LocalDateTime.now());

    }

    @Test
    void listOrders_Success() {

        List<Order> orderList = List.of(pendingOrder);
        when(orderRepository.findByCustomerIdAndCreateDateBetween(
                pendingOrder.getCustomerId(), startDate, endDate))
                .thenReturn(orderList);
        when(orderMapper.fromOrder(pendingOrder)).thenReturn(pendingBuyOrderResponseDto);

        // When
        List<OrderResponseDto> responses = orderService.listOrders(pendingOrder.getCustomerId(), startDate, endDate);

        // Then
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(pendingOrder.getCustomerId(), responses.get(0).customerId());

        verify(orderRepository).findByCustomerIdAndCreateDateBetween(
                pendingOrder.getCustomerId(), startDate, endDate);
    }

    @Test
    void createBuyOrder_Success() {
        // Given
        when(assetServiceClient.getAssetByCustomerIdAndAssetName(String.valueOf(buyOrderRequest.customerId()), "TRY"))
                .thenReturn(Optional.of(tryAsset));
        when(orderMapper.fromOrder(pendingOrder)).thenReturn(pendingBuyOrderResponseDto);
        when(orderMapper.toOrder(buyOrderRequest)).thenReturn(pendingOrder);


        when(orderRepository.save(any(Order.class))).thenReturn(pendingOrder);


        OrderResponseDto response = orderService.createOrder(buyOrderRequest);


        assertNotNull(response);
        assertEquals(OrderStatus.PENDING, response.orderStatus());
        assertEquals(buyOrderRequest.customerId(), response.customerId());
        assertEquals(buyOrderRequest.assetName(), response.assetName());
        assertEquals(buyOrderRequest.orderSide(), response.orderSide());

        verify(assetServiceClient).getAssetByCustomerIdAndAssetName(String.valueOf(buyOrderRequest.customerId()), "TRY");
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void createBuyOrder_InsufficientFunds() {
        // Given
        AssetDto lowBalanceTryAsset = new AssetDto(1L, buyOrderRequest.customerId(),
                                        "TRY", BigDecimal.valueOf(100), BigDecimal.valueOf(100));


        when(assetServiceClient.getAssetByCustomerIdAndAssetName(String.valueOf(buyOrderRequest.customerId()), "TRY"))
                .thenReturn(Optional.of(lowBalanceTryAsset));


        // When & Then
        assertThrows(InsufficientBalanceException.class, () -> orderService.createOrder(buyOrderRequest));

        verify(assetServiceClient).getAssetByCustomerIdAndAssetName(String.valueOf(buyOrderRequest.customerId()), "TRY");
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void createSellOrder_Success() {
        // Given
        when(assetServiceClient.getAssetByCustomerIdAndAssetName(String.valueOf(sellOrderRequest.customerId()), sellOrderRequest.assetName()))
                .thenReturn(Optional.of(googlAsset));

        Order sellOrder = new Order(sellOrderRequest.customerId(), sellOrderRequest.assetName(),
                                    OrderSide.SELL, sellOrderRequest.size(), sellOrderRequest.price(), OrderStatus.PENDING, LocalDateTime.now());
        sellOrder.setId(2L);

        when(orderMapper.fromOrder(sellOrder)).thenReturn(pendingSellOrderResponseDto);
        when(orderMapper.toOrder(sellOrderRequest)).thenReturn(sellOrder);

        when(orderRepository.save(any(Order.class))).thenReturn(sellOrder);

        // When
        OrderResponseDto response = orderService.createOrder(sellOrderRequest);

        // Then
        assertNotNull(response);
        assertEquals(OrderStatus.PENDING, response.orderStatus());
        assertEquals(sellOrderRequest.customerId(), response.customerId());
        assertEquals(sellOrderRequest.assetName(), response.assetName());
        assertEquals(OrderSide.SELL, response.orderSide());

        verify(assetServiceClient).getAssetByCustomerIdAndAssetName(String.valueOf(sellOrderRequest.customerId()), sellOrderRequest.assetName());
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void createSellOrder_InsufficientAsset() {

        AssetDto lowBalanceAsset = new AssetDto(2L, sellOrderRequest.customerId(), sellOrderRequest.assetName(),
                                                BigDecimal.valueOf(3), BigDecimal.valueOf(3));

        when(assetServiceClient.getAssetByCustomerIdAndAssetName(String.valueOf(sellOrderRequest.customerId()), sellOrderRequest.assetName()))
                .thenReturn(Optional.of(lowBalanceAsset));

        Order sellOrder = new Order(sellOrderRequest.customerId(), sellOrderRequest.assetName(),
                OrderSide.SELL, sellOrderRequest.size(), sellOrderRequest.price(), OrderStatus.PENDING, LocalDateTime.now());
        sellOrder.setId(2L);

        assertThrows(InsufficientBalanceException.class, () -> orderService.createOrder(sellOrderRequest));

        verify(assetServiceClient).getAssetByCustomerIdAndAssetName(String.valueOf(sellOrderRequest.customerId()), sellOrderRequest.assetName());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void cancelOrder_Success() {
        // Given
        when(orderRepository.findByIdAndStatus(pendingOrder.getId(), OrderStatus.PENDING))
                .thenReturn(Optional.of(pendingOrder));
        when(assetServiceClient.getAssetByCustomerIdAndAssetName(String.valueOf(pendingOrder.getCustomerId()), "TRY"))
                .thenReturn(Optional.of(tryAsset));

        // When
        orderService.cancelOrder(pendingOrder.getId());

        // Then
        verify(orderRepository).findByIdAndStatus(pendingOrder.getId(), OrderStatus.PENDING);
        verify(orderRepository).save(any(Order.class));
        verify(assetServiceClient).getAssetByCustomerIdAndAssetName(String.valueOf(pendingOrder.getCustomerId()), "TRY");
    }

    @Test
    void cancelOrder_OrderNotFound() {
        // Given
        Long nonExistingOrderId = 9999L;
        when(orderRepository.findByIdAndStatus(nonExistingOrderId, OrderStatus.PENDING))
                .thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> orderService.cancelOrder(nonExistingOrderId));

        verify(orderRepository).findByIdAndStatus(nonExistingOrderId, OrderStatus.PENDING);
        verify(orderRepository, never()).save(any(Order.class));
    }

}
