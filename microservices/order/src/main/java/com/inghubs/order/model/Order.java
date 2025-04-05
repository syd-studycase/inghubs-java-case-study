package com.inghubs.order.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
public class Order {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(name = "customer_id")
        private Long customerId;

        @Column(name = "asset_name")
        private String assetName;

        @Column(name = "order_side")
        @Enumerated(EnumType.STRING)
        private OrderSide orderSide;

        @Column(name = "order_size")
        private BigDecimal orderSize;

        private BigDecimal price;

        @Enumerated(EnumType.STRING)
        private OrderStatus status;

        @Column(name = "create_date")
        private LocalDateTime createDate;

        public Order() {}

        public Order(Long customerId, String assetName, OrderSide orderSide, BigDecimal orderSize, BigDecimal price, OrderStatus status, LocalDateTime createDate) {
                this.customerId = customerId;
                this.assetName = assetName;
                this.orderSide = orderSide;
                this.orderSize = orderSize;
                this.price = price;
                this.status = status;
                this.createDate = createDate;
        }

        public Long getId() {
                return id;
        }

        public void setId(Long id) {
                this.id = id;
        }

        public Long getCustomerId() {
                return customerId;
        }

        public void setCustomerId(Long customerId) {
                this.customerId = customerId;
        }

        public String getAssetName() {
                return assetName;
        }

        public void setAssetName(String assetName) {
                this.assetName = assetName;
        }

        public OrderSide getOrderSide() {
                return orderSide;
        }

        public void setOrderSide(OrderSide orderSide) {
                this.orderSide = orderSide;
        }

        public BigDecimal getOrderSize() {
                return orderSize;
        }

        public void setOrderSize(BigDecimal orderSize) {
                this.orderSize = orderSize;
        }

        public BigDecimal getPrice() {
                return price;
        }

        public void setPrice(BigDecimal price) {
                this.price = price;
        }

        public OrderStatus getStatus() {
                return status;
        }

        public void setStatus(OrderStatus status) {
                this.status = status;
        }

        public LocalDateTime getCreateDate() {
                return createDate;
        }

        public void setCreateDate(LocalDateTime createDate) {
                this.createDate = createDate;
        }

        @Override
        public String toString() {
                return "Order{" +
                        "id=" + id +
                        ", customerId=" + customerId +
                        ", assetName='" + assetName + '\'' +
                        ", orderSide=" + orderSide +
                        ", size=" + orderSize +
                        ", price=" + price +
                        ", status=" + status +
                        ", createDate=" + createDate +
                        '}';
        }
}
