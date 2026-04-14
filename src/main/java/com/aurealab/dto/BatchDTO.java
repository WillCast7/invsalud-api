package com.aurealab.dto;

import jakarta.persistence.Column;

import java.time.LocalDateTime;

public record BatchDTO(
    Long id,
    String code,
    Boolean isActive
){}
