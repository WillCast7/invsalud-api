package com.aurealab.dto.CashRegister;

import java.math.BigDecimal;

public record ProductDTO(
        Long id,
        String name,
        BigDecimal basePrice,
        String description,
        boolean isActive
) {
}
