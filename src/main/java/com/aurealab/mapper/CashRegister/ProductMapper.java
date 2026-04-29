package com.aurealab.mapper.CashRegister;

import com.aurealab.dto.CashRegister.ProductDTO;
import com.aurealab.model.cashRegister.entity.ProductEntity;

public class ProductMapper {
    private ProductMapper() {
    }

    /* ===================== Entity -> DTO ===================== */
    public static ProductDTO toDto(ProductEntity entity) {
        if (entity == null) return null;

        return new ProductDTO(
                entity.getId(),
                entity.getName(),
                entity.getBasePrice(),
                entity.getDescription(),
                entity.getType(),
                entity.isActive(),
                entity.isAppliesToCase(),
                entity.getCategoryId(),
                entity.isUseThirdParty(),
                null
        );
    }

    /* ===================== DTO -> Entity ===================== */
    public static ProductEntity toEntity(ProductDTO dto) {
        if (dto == null) return null;

        ProductEntity entity = new ProductEntity();
        entity.setId(dto.id());
        entity.setName(dto.name());
        entity.setBasePrice(dto.basePrice());
        entity.setDescription(dto.description());
        entity.setType(dto.type());
        entity.setActive(dto.isActive());
        entity.setAppliesToCase(dto.appliesToCase());
        entity.setCategoryId(dto.categoryId());
        entity.setUseThirdParty(dto.useThirdParty());
        return entity;
    }
}
