package com.inghubs.order.controller;

import com.inghubs.order.dto.CreateOrderRequestDto;
import com.inghubs.order.dto.OrderResponseDto;
import com.inghubs.order.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/create")
    public ResponseEntity<OrderResponseDto> createOrder(@RequestBody CreateOrderRequestDto createOrderRequestDto) {
        return ResponseEntity.ok(orderService.createOrder(createOrderRequestDto));
    }

    @GetMapping
    public ResponseEntity<List<OrderResponseDto>> listOrders(@RequestParam Long customerId,
                                                             @RequestParam LocalDateTime start,
                                                             @RequestParam LocalDateTime end) {
        return ResponseEntity.ok(orderService.listOrders(customerId, start, end));
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> cancelOrder(@PathVariable Long orderId) {
        orderService.cancelOrder(orderId);
        return ResponseEntity.noContent().build();
    }
}
