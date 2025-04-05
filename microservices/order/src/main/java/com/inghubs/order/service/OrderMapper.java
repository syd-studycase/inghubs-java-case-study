package com.inghubs.order.service;

import com.inghubs.order.dto.CreateOrderRequestDto;
import com.inghubs.order.dto.OrderResponseDto;
import com.inghubs.order.model.Order;
import com.inghubs.order.model.OrderStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class OrderMapper {

    public Order toOrder(CreateOrderRequestDto createOrderRequestDto) {

        if (createOrderRequestDto == null) {
            return null;
        }

        return new Order(
                createOrderRequestDto.customerId(),
                createOrderRequestDto.assetName(),
                createOrderRequestDto.orderSide(),
                createOrderRequestDto.size(),
                createOrderRequestDto.price(),
                OrderStatus.PENDING,
                LocalDateTime.now());
    }

    public OrderResponseDto fromOrder(Order order) {
        return new OrderResponseDto(
                order.getId(),
                order.getCustomerId(),
                order.getAssetName(),
                order.getOrderSide(),
                order.getPrice(),
                order.getOrderSize(),
                order.getStatus(),
                order.getCreateDate()
        );
    }
}
