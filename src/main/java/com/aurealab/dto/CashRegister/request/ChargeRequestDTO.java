package com.aurealab.dto.CashRegister.request;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ChargeRequestDTO(
        Long id,
        Long thirdParty,
        BigDecimal totalAmount,
        String status,
        String description
) {
}
