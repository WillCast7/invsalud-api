package com.aurealab.dto.CashRegister;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

public record CashSessionDTO(
            Long id,
            OffsetDateTime openedAt,
            OffsetDateTime closedAt,
            LocalDate businessDate,
            BigDecimal openingAmount,
            BigDecimal closingAmount,
            Long openedBySystemUserId,
            Long closedBySystemUserId,
            String observations,
            String status
            ) {
}
