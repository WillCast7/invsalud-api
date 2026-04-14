package com.aurealab.model.inventory.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "order_items")
public class OrderItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private OrderEntity order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prescription_inventory_id")
    private PrescriptionInventoryEntity inventory;

    @Column(name = "price_unit")
    private BigDecimal priceUnit;

    private Long units;

    @Column(name = "price_total")
    private BigDecimal priceTotal;

    @Column(name = "start_serial_sold")
    private Long startSerialSold;

    @Column(name = "end_serial_sold")
    private Long endSerialSold;
}
