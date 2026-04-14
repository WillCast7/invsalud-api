package com.aurealab.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record RecipeInventoryDTO(
    Long id,
    BigDecimal priceUnit,
    BigDecimal total,
    int units
) {
}
