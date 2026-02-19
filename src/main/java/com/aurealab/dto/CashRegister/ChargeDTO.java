package com.aurealab.dto.CashRegister;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record ChargeDTO(
        Long id,
        ThirdPartyDTO thirdParty,
        BigDecimal totalAmount,
        BigDecimal paidAmount,
        BigDecimal balance,
        String status,
        String description,
        Long createdBySystemUserId
) {
}
