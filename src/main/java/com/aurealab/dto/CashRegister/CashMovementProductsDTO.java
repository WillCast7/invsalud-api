package com.aurealab.dto.CashRegister;

import com.aurealab.dto.CashRegister.response.CashMovementResponseDTO;

import java.math.BigDecimal;

public record CashMovementProductsDTO(
        Long id,
        String product,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal subtotal,
        CashMovementResponseDTO cashMovement

) {
}
