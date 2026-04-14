package com.aurealab.dto.CashRegister.request;

import java.math.BigDecimal;

public record CashMovementProductsRequestDTO(
        Long id,
        String product,
        Integer quantity,
        CashMovementRequestDTO cashMovement,
        BigDecimal unitPrice,
        BigDecimal subtotal
) {
}
