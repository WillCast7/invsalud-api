package com.aurealab.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record DocumentTemplateDTO(
    UUID id,
    String name,
    String documentType,
    String category,
    String htmlContent,
    String cssContent,
    String sectionsState,
    Boolean isDefault,
    Boolean isActive,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    Long createdBy,
    Integer version
) {}
