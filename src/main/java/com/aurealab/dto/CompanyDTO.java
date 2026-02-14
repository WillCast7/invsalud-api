package com.aurealab.dto;

import jakarta.persistence.Column;

import java.time.LocalDateTime;

public record CompanyDTO(
    Long id,
    String name,
    String legalName,
    String taxId,
    String email,
    String phone,
    String address,
    String country,
    String type,
    String city,
    String website,
    String logoUrl,
    String subscriptionPlan,
    LocalDateTime createdAt,
    Boolean isActive
) {}
