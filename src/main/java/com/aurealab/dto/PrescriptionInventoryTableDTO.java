package com.aurealab.dto;

import com.aurealab.dto.CashRegister.ProductDTO;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
public record PrescriptionInventoryTableDTO(
        Long id,
        String product,
        String presentation,
        String pharmaceuticalForm,
        String batch,
        BigDecimal purchasePrice,
        BigDecimal salePrice,
        Long totalUnits,
        Long availableUnits,
        LocalDate expirationDate,
        boolean isActive,
        boolean isDrawal,
        Long withdrawalBy,
        LocalDateTime withdrawnAt,
        String withdrawalCode,
        String withdrawalType
) {
}
