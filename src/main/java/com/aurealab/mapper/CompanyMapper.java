package com.aurealab.mapper;


import com.aurealab.dto.CompanyDTO;
import com.aurealab.model.aurea.entity.CompanyEntity;

import java.time.LocalDateTime;

public class CompanyMapper {
    private CompanyMapper() {
    }

    /* ===================== Entity -> DTO ===================== */
    public static CompanyDTO toDto(CompanyEntity entity) {
        if (entity == null) return null;

        return new CompanyDTO(
                entity.getId(),
                entity.getName(),
                entity.getLegalName(),
                entity.getTaxId(),
                entity.getEmail(),
                entity.getPhone(),
                entity.getAddress(),
                entity.getCountry(),
                entity.getType(),
                entity.getCity(),
                entity.getWebsite(),
                entity.getLogoUrl(),
                entity.getSubscriptionPlan(),
                entity.getCreatedAt(),
                entity.getIsActive()
        );
    }

    /* ===================== DTO -> Entity ===================== */
    public static CompanyEntity toEntity(CompanyDTO dto) {
        if (dto == null) return null;

        CompanyEntity entity = new CompanyEntity();
        entity.setId(dto.id());
        entity.setName(dto.name());
        entity.setLegalName(dto.legalName());
        entity.setTaxId(dto.taxId());
        entity.setEmail(dto.email());
        entity.setPhone(dto.phone());
        entity.setAddress(dto.address());
        entity.setCountry(dto.country());
        entity.setType(dto.type());
        entity.setCity(dto.city());
        entity.setWebsite(dto.website());
        entity.setLogoUrl(dto.logoUrl());
        entity.setSubscriptionPlan(dto.subscriptionPlan());
        entity.setCreatedAt(dto.createdAt());
        entity.setIsActive(dto.isActive());
        return entity;
    }
}
