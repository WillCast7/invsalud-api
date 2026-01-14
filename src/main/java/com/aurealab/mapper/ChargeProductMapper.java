package com.aurealab.mapper;

import com.aurealab.dto.CashRegister.ChargeProductDTO;
import com.aurealab.model.cashRegister.entity.ChargeProductEntity;
import com.aurealab.model.cashRegister.entity.ProductEntity;

public class ChargeProductMapper {

    private ChargeProductMapper() {
    }

    /* ===================== Entity -> DTO ===================== */
    public static ChargeProductDTO toDto(ChargeProductEntity entity) {
        if (entity == null) return null;

        return new ChargeProductDTO(
                entity.getId(),
                entity.getProductEntity() != null
                        ? entity.getProductEntity().getId()
                        : null,
                entity.getQuantity(),
                entity.getUnitPrice(),
                entity.getSubtotal()
        );
    }

    /* ===================== DTO -> Entity ===================== */
    public static ChargeProductEntity toEntity(
            ChargeProductDTO dto,
            ProductEntity product
    ) {
        if (dto == null) return null;

        ChargeProductEntity entity = new ChargeProductEntity();
        entity.setId(dto.id());
        entity.setProductEntity(product);
        entity.setQuantity(dto.quantity());
        entity.setUnitPrice(dto.unitPrice());

        return entity;
    }
}
