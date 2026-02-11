package com.aurealab.model.cashRegister.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cashmovement_products")
public class CashMovementProductsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cashmovement_id")
    private CashMovementEntity cashMovement;

    private String product;

    private Integer quantity;

    @Column(precision = 19, scale = 4)
    private BigDecimal unitPrice; // Precio histórico congelado

    // El subtotal se puede calcular en el getter para asegurar consistencia
    public BigDecimal getSubtotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
    public CashMovementProductsEntity(Long id){
        this.id = id;
    }
}
