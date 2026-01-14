package com.aurealab.dto.CashRegister;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record CashMovementDTO (
        Long id,
        CashSessionDTO cashSession,
        ChargeDTO charge,
        ThirdPartyDTO thirdParty,
        String type,
        BigDecimal amount,
        String concept,
        String paymentMethod,
        String referenceNumber,
        boolean isVoid,
        OffsetDateTime createdAt,
        Long createdBySystemUserId
){
}
