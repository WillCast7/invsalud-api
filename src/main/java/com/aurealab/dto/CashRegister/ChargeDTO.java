package com.aurealab.dto.CashRegister;

import java.math.BigDecimal;
import java.util.List;

public record ChargeDTO(
        Long id,
        Long personId,
        BigDecimal totalAmount,
        String status,
        String description,
        List<ChargeProductDTO> itemIds
) {
}
