package com.inghubs.order.repository;

import com.inghubs.order.model.Order;
import com.inghubs.order.model.OrderSide;
import com.inghubs.order.model.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("dev")
@DataJpaTest
public class OrderRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private OrderRepository orderRepository;

    private LocalDateTime now;

    @BeforeEach
    void setup() {

        now = LocalDateTime.now();
    }

    @Test
    void findByCustomerIdAndCreateDateBetween_ReturnsOrders_WhenOrdersExistInDateRange() {

        LocalDateTime start = now.minusDays(7);
        LocalDateTime end = now.plusDays(1);


        Order order1 = new Order(1L, "TRY", OrderSide.BUY, BigDecimal.valueOf(10), BigDecimal.valueOf(100), OrderStatus.PENDING, now.minusDays(5));
        Order order2 = new Order(1L, "TRY", OrderSide.BUY, BigDecimal.valueOf(10), BigDecimal.valueOf(100), OrderStatus.PENDING, now.minusDays(2));
        Order order3 = new Order(1L, "TRY", OrderSide.BUY, BigDecimal.valueOf(10), BigDecimal.valueOf(100), OrderStatus.PENDING, now.minusDays(1));

        Order orderOutsideRange = new Order(1L, "TRY", OrderSide.BUY,
                BigDecimal.valueOf(10), BigDecimal.valueOf(100), OrderStatus.PENDING, now.minusDays(10));

        Order orderOtherCustomer = new Order(2L, "TRY", OrderSide.BUY, BigDecimal.valueOf(10),
                BigDecimal.valueOf(100), OrderStatus.PENDING, now.minusDays(2));

        entityManager.persist(order1);
        entityManager.persist(order2);
        entityManager.persist(order3);
        entityManager.persist(orderOutsideRange);
        entityManager.persist(orderOtherCustomer);
        entityManager.flush();

        List<Order> foundOrders = orderRepository.findByCustomerIdAndCreateDateBetween(
                1L, start, end);

        assertEquals(3, foundOrders.size());
        assertTrue(foundOrders.contains(order1));
        assertTrue(foundOrders.contains(order2));
        assertTrue(foundOrders.contains(order3));
        assertFalse(foundOrders.contains(orderOutsideRange));
        assertFalse(foundOrders.contains(orderOtherCustomer));

    }

    @Test
    void findByCustomerIdAndCreateDateBetween_ReturnsEmptyList_WhenNoOrdersExistInDateRange() {

        LocalDateTime start = now.minusDays(3);
        LocalDateTime end = now.minusDays(2);

        Order order1 = new Order(1L, "TRY", OrderSide.BUY, BigDecimal.valueOf(10), BigDecimal.valueOf(100), OrderStatus.PENDING, now.minusDays(5));
        Order order2 = new Order(1L, "TRY", OrderSide.BUY, BigDecimal.valueOf(10), BigDecimal.valueOf(100), OrderStatus.PENDING, now.minusDays(1));

        entityManager.persist(order1);
        entityManager.persist(order2);
        entityManager.flush();

        List<Order> foundOrders = orderRepository.findByCustomerIdAndCreateDateBetween(
                1L, start, end);

        assertTrue(foundOrders.isEmpty());

    }

    @Test
    void findByIdAndStatus_ReturnsOrder_WhenOrderExistsWithGivenIdAndStatus() {

        Order pendingOrder = new Order(1L, "TRY", OrderSide.BUY, BigDecimal.valueOf(10), BigDecimal.valueOf(100), OrderStatus.PENDING, now);
        Order matchedOrder = new Order(1L, "TRY", OrderSide.BUY, BigDecimal.valueOf(10), BigDecimal.valueOf(100), OrderStatus.MATCHED, now);

        entityManager.persist(pendingOrder);
        entityManager.persist(matchedOrder);
        entityManager.flush();

        Optional<Order> foundPendingOrder = orderRepository.findByIdAndStatus(
                pendingOrder.getId(), OrderStatus.PENDING);
        Optional<Order> foundMatchedOrder = orderRepository.findByIdAndStatus(
                matchedOrder.getId(), OrderStatus.MATCHED);
        Optional<Order> nonExistingStatusOrder = orderRepository.findByIdAndStatus(
                matchedOrder.getId(), OrderStatus.PENDING);

        assertTrue(foundPendingOrder.isPresent());
        assertEquals(pendingOrder.getId(), foundPendingOrder.get().getId());
        assertEquals(OrderStatus.PENDING, foundPendingOrder.get().getStatus());

        assertTrue(foundMatchedOrder.isPresent());
        assertEquals(matchedOrder.getId(), foundMatchedOrder.get().getId());
        assertEquals(OrderStatus.MATCHED, foundMatchedOrder.get().getStatus());

        assertFalse(nonExistingStatusOrder.isPresent());

    }

    @Test
    void findByIdAndStatus_ReturnsEmptyOptional_WhenOrderDoesNotExist() {

        Long nonExistingOrderId = 999L;

        Optional<Order> result = orderRepository.findByIdAndStatus(nonExistingOrderId, OrderStatus.PENDING);

        assertFalse(result.isPresent());

    }
}
