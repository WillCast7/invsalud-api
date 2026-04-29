package com.aurealab.dto.CashRegister;

import com.aurealab.dto.CashRegister.response.CashMovementResponseDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

public record CashMovementItemDTO(
        Long id,
        ProductDTO product,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal subtotal,
        LocalDateTime statusDoneAt,
        String status

) {
}
