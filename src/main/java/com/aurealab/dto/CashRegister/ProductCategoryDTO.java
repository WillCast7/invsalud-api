package com.aurealab.dto.CashRegister;

import jakarta.persistence.Column;

import java.time.LocalDateTime;

public record ProductCategoryDTO(
        Long id,
         String name,
         String description,
         boolean isActive,
         LocalDateTime createdAt,
         String fullName
) {
}
