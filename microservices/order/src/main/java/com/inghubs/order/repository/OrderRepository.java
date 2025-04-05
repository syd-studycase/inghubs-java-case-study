package com.inghubs.order.repository;

import com.inghubs.order.model.Order;
import com.inghubs.order.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByCustomerIdAndCreateDateBetween(Long customerId, LocalDateTime start, LocalDateTime end);

    Optional<Order> findByIdAndStatus(Long orderId, OrderStatus orderStatus);
}
