package com.aurealab.dto;

import com.aurealab.dto.CashRegister.ProductDTO;
import com.aurealab.model.inventory.entity.BatchEntity;
import com.aurealab.model.inventory.entity.ProductEntity;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
public record PrescriptionInventoryDTO(
        Long id,
        ProductDTO product,
        BatchDTO batch,
        BigDecimal purchasePrice,
        BigDecimal salePrice,
        int totalUnits,
        int availableUnits,
        LocalDate expirationDate,
        String createdBy,
        LocalDateTime createdAt,
        boolean isActive,
        boolean isDrawal,
        Long withdrawalBy,
        LocalDateTime withdrawnAt,
        String withdrawalCode,
        String withdrawalType
) {
}
