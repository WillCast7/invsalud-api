package com.aurealab.mapper.inventory;

import com.aurealab.dto.CashRegister.ProductDTO;
import com.aurealab.model.inventory.entity.ProductEntity;

public class ProductMapper {
    private ProductMapper() {
    }

    /* ===================== Entity -> DTO ===================== */
    public static ProductDTO toDto(ProductEntity entity) {
        if (entity == null) return null;

        return new ProductDTO(
                entity.getId(),
                entity.getName(),
                entity.getCode(),
                entity.getConcentration(),
                entity.getPresentation(),
                entity.getPharmaceuticalForm(),
                entity.getIsActive(),
                entity.getIsPublicHealth(),
                entity.getDetails(),
                entity.getCreatedAt(),
                entity.getCreatedBy()
        );
    }

    /* ===================== DTO -> Entity ===================== */
    public static ProductEntity toEntity(ProductDTO dto) {
        if (dto == null) return null;

        ProductEntity entity = new ProductEntity();
        entity.setId(dto.id());
        entity.setName(dto.name());
        entity.setCode(dto.code());
        entity.setConcentration(dto.concentration());
        entity.setPresentation(dto.presentation());
        entity.setPharmaceuticalForm(dto.pharmaceuticalForm());
        entity.setIsActive(dto.isActive());
        entity.setIsPublicHealth(dto.isPublicHealth());
        entity.setDetails(dto.details());
        entity.setCreatedAt(dto.createdAt());
        entity.setCreatedBy(dto.createdBy());

        return entity;
    }
}
