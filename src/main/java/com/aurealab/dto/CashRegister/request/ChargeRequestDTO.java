package com.aurealab.dto.CashRegister.request;

import com.aurealab.dto.CashRegister.CashMovementProductsDTO;
import com.aurealab.dto.CashRegister.ThirdPartyDTO;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record ChargeRequestDTO(
        Long id,
        Long thirdParty,
        BigDecimal totalAmount,
        String status,
        String description
) {
}
