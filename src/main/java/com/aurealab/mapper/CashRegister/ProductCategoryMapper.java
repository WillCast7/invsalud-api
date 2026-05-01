package com.aurealab.mapper.CashRegister;

import com.aurealab.dto.CashRegister.ProductCategoryDTO;
import com.aurealab.model.cashRegister.entity.ProductCategoryEntity;

public class ProductCategoryMapper {
    private ProductCategoryMapper() {
    }

    /* ===================== Entity -> DTO ===================== */
    public static ProductCategoryDTO toDto(ProductCategoryEntity entity) {
        if (entity == null) return null;

        return new ProductCategoryDTO(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.isActive(),
                entity.getCreatedAt(),
                entity.getFullName()
        );
    }

    /* ===================== DTO -> Entity ===================== */
    public static ProductCategoryEntity toEntity(ProductCategoryDTO dto) {
        if (dto == null) return null;

        ProductCategoryEntity entity = new ProductCategoryEntity();
        entity.setId(dto.id());
        entity.setName(dto.name());
        entity.setDescription(dto.description());
        entity.setActive(dto.isActive());
        entity.setCreatedAt(dto.createdAt());
        entity.setFullName(dto.fullName());
        return entity;
    }
}
