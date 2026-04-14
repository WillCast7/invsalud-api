package com.aurealab.dto.tables;

import java.math.BigDecimal;

public record CashMovementTableDTO(
        Long id,
        String customerName,
        String type,
        BigDecimal expectedAmount,
        BigDecimal receivedAmount,
        String concept,
        String product,
        String paymentMethod,
        boolean isVoid,
        String referenceNumber,
        String color
        ) {
}
