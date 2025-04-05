package com.inghubs.order.service;

import com.inghubs.order.dto.AssetDto;
import com.inghubs.order.dto.UpdateAssetRequestDto;
import com.inghubs.order.proxy.AssetServiceClient;
import com.inghubs.order.dto.CreateOrderRequestDto;
import com.inghubs.order.dto.OrderResponseDto;
import com.inghubs.order.exception.AssetNotFoundException;
import com.inghubs.order.exception.InsufficientBalanceException;
import com.inghubs.order.exception.OrderNotFoundException;
import com.inghubs.order.model.Order;
import com.inghubs.order.model.OrderSide;
import com.inghubs.order.model.OrderStatus;
import com.inghubs.order.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final OrderRepository orderRepository;
    private final AssetServiceClient assetServiceClient;
    private final OrderMapper orderMapper;
    private static final String TRY_ASSET = "TRY";

    public OrderService(OrderRepository orderRepository, AssetServiceClient assetServiceClient, OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.assetServiceClient = assetServiceClient;
        this.orderMapper = orderMapper;
    }

    public List<OrderResponseDto> listOrders(Long customerId, LocalDateTime start, LocalDateTime end) {
        return orderRepository.findByCustomerIdAndCreateDateBetween(customerId, start, end)
                .stream()
                .map(this.orderMapper::fromOrder)
                .collect(Collectors.toList());
    }

    @Transactional
    public OrderResponseDto createOrder(CreateOrderRequestDto createOrderRequestDto) {

        logger.debug("Creating order for customer: {}", createOrderRequestDto.customerId());


        if (createOrderRequestDto.orderSide() == OrderSide.BUY) {
           return createBuyOrder(createOrderRequestDto);
        } else {
            return createSellOrder(createOrderRequestDto);
        }

    }

    private OrderResponseDto createBuyOrder(CreateOrderRequestDto createOrderRequestDto) {

        AssetDto tryAssetDto = assetServiceClient.getAssetByCustomerIdAndAssetName(String.valueOf(createOrderRequestDto.customerId()), TRY_ASSET)
                .orElseThrow(() -> new AssetNotFoundException("Customer does not have TRY asset"));

        BigDecimal requiredAmount = createOrderRequestDto.price().multiply(createOrderRequestDto.size());

        if (tryAssetDto.usableSize().compareTo(requiredAmount) < 0) {
            throw new InsufficientBalanceException("Insufficient TRY balance for order");
        }

        // Update usable size
        var updatedUsableSize = tryAssetDto.usableSize().subtract(requiredAmount);
        UpdateAssetRequestDto updateAssetRequestDto = new UpdateAssetRequestDto(tryAssetDto.id(),
                                                            tryAssetDto.customerId(), tryAssetDto.assetName(),
                                                            tryAssetDto.size(), updatedUsableSize);

        assetServiceClient.updateAsset(updateAssetRequestDto);
        logger.debug("Updating asset for customer: {}", updateAssetRequestDto.customerId());

        // Create Buy Order
        Order order = orderMapper.toOrder(createOrderRequestDto);

        Order savedOrder = orderRepository.save(order);
        logger.debug("Saving buy order: {}", savedOrder);

        return orderMapper.fromOrder(savedOrder);

    }

    private OrderResponseDto createSellOrder(CreateOrderRequestDto createOrderRequestDto) {

        //
        AssetDto assetDto = assetServiceClient.getAssetByCustomerIdAndAssetName(String.valueOf(createOrderRequestDto.customerId()), createOrderRequestDto.assetName())
                .orElseThrow(() -> new AssetNotFoundException("Customer does not have required asset"));

        if (assetDto.usableSize().compareTo(createOrderRequestDto.size()) < 0) {
            throw new InsufficientBalanceException("Insufficient asset balance for sell order");
        }

        // Update usable size
        var updatedUsableSize = assetDto.usableSize().subtract(createOrderRequestDto.size());
        UpdateAssetRequestDto updateAssetRequestDto = new UpdateAssetRequestDto(assetDto.id(),
                assetDto.customerId(), assetDto.assetName(),
                assetDto.size(), updatedUsableSize);

        assetServiceClient.updateAsset(updateAssetRequestDto);
        logger.debug("Updating asset for customer: {}", updateAssetRequestDto.customerId());

        // Create Sell Order
        Order order = orderMapper.toOrder(createOrderRequestDto);

        Order savedOrder = orderRepository.save(order);
        logger.debug("Saving sell order: {}", savedOrder);

        return orderMapper.fromOrder(savedOrder);

    }

    public void cancelOrder(Long orderId){
        logger.info("Cancelling order with ID: {}", orderId);

        Order order = orderRepository.findByIdAndStatus(orderId, OrderStatus.PENDING)
                .orElseThrow(() -> new OrderNotFoundException("Pending order not found with ID: " + orderId));

        // Update order status
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

        // Return funds to usable balance
        if (order.getOrderSide() == OrderSide.BUY) {
            returnTryFundsForCancelledBuyOrder(order);
        } else {
            returnAssetForCancelledSellOrder(order);
        }

        logger.info("Order cancelled successfully: {}", orderId);
    }

    private void returnTryFundsForCancelledBuyOrder(Order order) {
        AssetDto tryAssetDto = assetServiceClient.getAssetByCustomerIdAndAssetName(String.valueOf(order.getCustomerId()), TRY_ASSET)
                .orElseThrow(() -> new AssetNotFoundException("Customer does not have TRY asset"));

        // Calculate return amount
        BigDecimal returnAmount = order.getOrderSize().multiply(order.getPrice());
        BigDecimal updatedUsableSize = tryAssetDto.usableSize().add(returnAmount);

        UpdateAssetRequestDto updateAssetRequestDto = new UpdateAssetRequestDto(tryAssetDto.id(),
                tryAssetDto.customerId(), tryAssetDto.assetName(),
                tryAssetDto.size(), updatedUsableSize);

        // Update TRY Asset usable size
        assetServiceClient.updateAsset(updateAssetRequestDto);
        logger.debug("Asset updated: {}", updateAssetRequestDto);
        logger.info("Updating asset for customer: {}", updateAssetRequestDto.customerId());

    }

    private void returnAssetForCancelledSellOrder(Order order) {
        AssetDto assetDto = assetServiceClient.getAssetByCustomerIdAndAssetName(String.valueOf(order.getCustomerId()), order.getAssetName())
                .orElseThrow(() -> new AssetNotFoundException("Customer does not have required asset"));

        // Calculate usable size
        BigDecimal updatedUsableSize = assetDto.usableSize().add(order.getOrderSize());

        UpdateAssetRequestDto updateAssetRequestDto = new UpdateAssetRequestDto(assetDto.id(),
                assetDto.customerId(), assetDto.assetName(),
                assetDto.size(), updatedUsableSize);

        // Update Asset usable size
        assetServiceClient.updateAsset(updateAssetRequestDto);
        logger.debug("Asset updated: {}", updateAssetRequestDto);
        logger.info("Updating asset for customer: {}", updateAssetRequestDto.customerId());
    }
}
