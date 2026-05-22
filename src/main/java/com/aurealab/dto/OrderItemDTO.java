package com.aurealab.dto;

import java.math.BigDecimal;

public record OrderItemDTO(
        Long id,
        PrescriptionInventoryDTO inventory,
        BigDecimal priceUnit,
        Long units,
        BigDecimal priceTotal
) {
}
