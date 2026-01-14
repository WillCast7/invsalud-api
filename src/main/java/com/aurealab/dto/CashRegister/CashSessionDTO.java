package com.aurealab.dto.CashRegister;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record CashSessionDTO(
            Long id,
            OffsetDateTime openedAt,
            OffsetDateTime closedAt,
            BigDecimal openingAmount,
            BigDecimal closingAmount,
            Long openedBySystemUserId,
            Long closedBySystemUserId,
            String status
) {
}
