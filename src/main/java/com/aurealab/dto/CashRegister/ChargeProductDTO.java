package com.aurealab.dto.CashRegister;

import java.math.BigDecimal;

public record ChargeProductDTO(
        Long id,
        Long productId,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal subtotal
) {
}
