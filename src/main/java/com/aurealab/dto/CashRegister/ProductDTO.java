package com.aurealab.dto.CashRegister;

import java.time.LocalDateTime;

public record ProductDTO(
        Long id,
        String name,
        String code,
        String concentration,
        String presentation,
        String pharmaceuticalForm,
        Integer unitsAlert,
        Boolean isActive,
        Boolean isPublicHealth,
        String details,
        LocalDateTime createdAt,
        Long createdBy
) {
}
