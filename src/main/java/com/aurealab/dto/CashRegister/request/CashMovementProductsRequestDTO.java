package com.aurealab.dto.CashRegister.request;

import com.aurealab.dto.CashRegister.ProductDTO;

import java.math.BigDecimal;

public record CashMovementProductsRequestDTO(
        Long id,
        ProductDTO product,
        Integer quantity,
        CashMovementRequestDTO cashMovement,
        BigDecimal unitPrice,
        BigDecimal subtotal
) {
}
