package com.aurealab.dto.tables;

import java.math.BigDecimal;

public record CashMovementTableDTO(
        Long id,
        String customerName,
        String type,
        BigDecimal expectedAmount,
        BigDecimal receivedAmount,
        String concept,
        boolean isVoid,
        String referenceNumber,
        String color
        ) {
}
