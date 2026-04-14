package com.aurealab.model.inventory.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.engine.jdbc.batch.spi.Batch;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "prescription_inventories")
public class PrescriptionInventoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private ProductEntity product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batche_id")
    private BatchEntity batch;

    @Column(name = "purchase_price")
    private BigDecimal purchasePrice;

    @Column(name = "sale_price")
    private BigDecimal salePrice;

    @Column(name = "total_units")
    private int totalUnits;

    @Column(name = "available_units")
    private int availableUnits;

    @Column(name = "expiration_date")
    private LocalDate expirationDate;

    // Auditoría de Creación
    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "is_active")
    private boolean isActive; //activo?

    @Column(name = "is_drawal")
    private boolean isDrawal; // retidado?


    @Column(name = "withdrawal_by")
    private Long withdrawalBy;

    @Column(name = "withdrawal_at")
    private LocalDateTime withdrawnAt;

    @Column(name = "withdrawal_code")
    private String withdrawalCode;

    @Column(name = "withdrawal_type")
    private String withdrawalType;

    public PrescriptionInventoryEntity(Long id) {
        this.id = id;
    }
}
