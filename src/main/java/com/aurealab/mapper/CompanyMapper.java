package com.aurealab.mapper;


import com.aurealab.dto.CompanyDTO;
import com.aurealab.model.aurea.entity.CompanyEntity;

import java.time.LocalDateTime;

public class CompanyMapper {
    private CompanyMapper() {
    }

    /* ===================== Entity -> DTO ===================== */
    public static CompanyDTO toLogin(CompanyEntity entity) {
        if (entity == null) return null;

        return new CompanyDTO(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                entity.getLogoUrl(),
                null,
                null,
                null,
                null,
                entity.getNameApp(),
                null,
                null,
                0,
                false,
                null,
                0
        );
    }

    /* ===================== Entity -> DTO ===================== */
    public static CompanyDTO toDto(CompanyEntity entity) {
        if (entity == null) return null;

        return new CompanyDTO(
                entity.getId(),
                entity.getNit(),
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
                entity.getLogoOrder(),
                entity.getLogoSold(),
                entity.getLogoPurchasing(),
                entity.getSubscriptionPlan(),
                entity.getNameApp(),
                entity.getCreatedAt(),
                entity.getIsActive(),
                entity.getIva(),
                entity.getUseIva(),
                entity.getFooter(),
                entity.getDaysLimitResolution()
        );
    }

    /* ===================== DTO -> Entity ===================== */
    public static CompanyEntity toEntity(CompanyDTO dto) {
        if (dto == null) return null;

        CompanyEntity entity = new CompanyEntity();
        entity.setId(dto.id());
        entity.setNit(dto.nit());
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
        entity.setLogoOrder(dto.logoOrder());
        entity.setLogoSold(dto.logoSold());
        entity.setLogoPurchasing(dto.logoPurchasing());
        entity.setNameApp(dto.nameApp());
        entity.setSubscriptionPlan(dto.subscriptionPlan());
        entity.setCreatedAt(dto.createdAt());
        entity.setIsActive(dto.isActive());
        entity.setIva(dto.iva());
        entity.setUseIva(dto.useIva());
        entity.setFooter(dto.footer());
        entity.setDaysLimitResolution(dto.daysLimitResolution());
        return entity;
    }
}
