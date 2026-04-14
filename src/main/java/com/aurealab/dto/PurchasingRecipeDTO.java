package com.aurealab.dto;

import com.aurealab.model.inventory.entity.PurchasingEntity;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

import java.math.BigDecimal;

public record PurchasingRecipeDTO(
        Long id,
        BigDecimal priceUnit,
        int units,
        BigDecimal priceTotal
) {
}
