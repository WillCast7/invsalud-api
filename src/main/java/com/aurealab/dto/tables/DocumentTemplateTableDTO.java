package com.aurealab.dto.tables;

import java.time.LocalDateTime;
import java.util.UUID;

public record DocumentTemplateTableDTO(
    UUID id,
    String name,
    String documentType,
    String category,
    Boolean isDefault,
    Boolean isActive,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    Integer version
) {}
