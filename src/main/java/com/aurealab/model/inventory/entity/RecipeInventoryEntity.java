package com.aurealab.model.inventory.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "recipes_inventory")
public class RecipeInventoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal price;

    @Column(name = "total_units")
    private int totalUnits;

    @Column(name = "avaliable_units")
    private BigDecimal avaliableUnits;
}
