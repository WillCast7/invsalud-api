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
@Table(name = "purchasing_items")
public class PurchasingItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "purchasing_id")
    private PurchasingEntity purchasing;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private ProductEntity product; // <-- Asegúrate de que esté mapeado

    @ManyToOne
    @JoinColumn(name = "batch_id") // <-- FALTA: Relación con el lote
    private BatchEntity batch;

    @ManyToOne
    @JoinColumn(name = "prescription_inventory_id")
    private PrescriptionInventoryEntity inventory;

    @Column(name = "price_unit")
    private BigDecimal priceUnit; // Precio de compra

    @Column(name = "sell_price") // <-- FALTA: Precio al que se va a vender
    private BigDecimal sellPrice;

    private int units;

    @Column(name = "price_total")
    private BigDecimal priceTotal;

    @Column(name = "expiration_date") // <-- FALTA: Para el control de inventario
    private java.time.LocalDate expirationDate;
}
