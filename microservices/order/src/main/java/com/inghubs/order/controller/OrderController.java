package com.inghubs.order.controller;

import com.inghubs.order.dto.CreateOrderRequestDto;
import com.inghubs.order.dto.OrderResponseDto;
import com.inghubs.order.service.OrderService;
import org.springframework.http.HttpStatus;
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
        OrderResponseDto createdOrder = orderService.createOrder(createOrderRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder); // 201 Created
    }

    @GetMapping
    public ResponseEntity<List<OrderResponseDto>> listOrders(@RequestParam Long customerId,
                                                             @RequestParam LocalDateTime start,
                                                             @RequestParam LocalDateTime end) {
        List<OrderResponseDto> orderDtoList = orderService.listOrders(customerId, start, end);
        if (orderDtoList.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 No Content
        }
        return ResponseEntity.ok(orderDtoList); // 200 OK
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> cancelOrder(@PathVariable Long orderId) {
        boolean cancelled = orderService.cancelOrder(orderId);
        if (cancelled) {
            return ResponseEntity.noContent().build(); // 204 No Content
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 404 Not Found
        }
    }
}
