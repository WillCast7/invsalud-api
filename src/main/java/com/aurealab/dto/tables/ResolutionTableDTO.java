package com.aurealab.dto.tables;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ResolutionTableDTO(
    Long id,
    String thirdParty,
    String code,
    LocalDate startDate,
    LocalDate expirationDate,
    String description,
    Boolean isActive,
    LocalDateTime createdAt,
    String createdBy
) {
}
