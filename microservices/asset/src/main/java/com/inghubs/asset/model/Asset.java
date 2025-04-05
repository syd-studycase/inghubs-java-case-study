package com.inghubs.asset.model;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "assets", uniqueConstraints = {
@UniqueConstraint(columnNames = {"customer_id", "asset_name"})
})
public class Asset {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(name = "customer_id")
        private Long customerId;

        @Column(name = "asset_name")
        private String assetName;

        @Column(name = "asset_size")
        private BigDecimal size;

        @Column(name = "usable_size")
        private BigDecimal usableSize;

        public Asset() {

        }

        public Asset(Long customerId, String assetName, BigDecimal size, BigDecimal usableSize) {
                this.customerId = customerId;
                this.assetName = assetName;
                this.size = size;
                this.usableSize = usableSize;
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

        public BigDecimal getSize() {
                return size;
        }

        public void setSize(BigDecimal size) {
                this.size = size;
        }

        public BigDecimal getUsableSize() {
                return usableSize;
        }

        public void setUsableSize(BigDecimal usableSize) {
                this.usableSize = usableSize;
        }

        @Override
        public String toString() {
                return "Asset{" +
                        "id=" + id +
                        ", customerId=" + customerId +
                        ", assetName='" + assetName + '\'' +
                        ", size=" + size +
                        ", usableSize=" + usableSize +
                        '}';
        }
}
