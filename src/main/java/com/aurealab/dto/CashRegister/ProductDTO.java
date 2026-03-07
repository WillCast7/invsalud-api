package com.aurealab.dto.CashRegister;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record ProductDTO(
        Long id,
        String name,
        BigDecimal basePrice,
        String description,
        String type,
        boolean active
) {
}
