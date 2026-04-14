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
@Table(name = "purchasing_recipes")
public class PurchasingRecipeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "purchasing_id")
    private PurchasingEntity purchasing;

    @Column(name = "price_unit")
    private BigDecimal priceUnit;

    private int units;

    @Column(name = "price_total")
    private BigDecimal priceTotal;
}
