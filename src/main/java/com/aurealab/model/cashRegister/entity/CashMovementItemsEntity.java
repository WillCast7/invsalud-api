package com.aurealab.model.cashRegister.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cash_movement_items")
public class CashMovementItemsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cash_movement_id")
    private CashMovementEntity cashMovement;

    @Column(name = "product_name")
    private String productName;

    private Integer quantity;

    @Column(precision = 19, scale = 4, name = "unit_price")
    private BigDecimal unitPrice; // Precio histórico congelado

    public BigDecimal getSubtotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    @ManyToOne
    @JoinColumn(name = "product_id")
    private ProductEntity product;

    @Column(name = "status_done_at")
    private LocalDateTime statusDoneAt;

    private String status;

    public CashMovementItemsEntity(Long id){
        this.id = id;
    }
}
