package com.aurealab.mapper.CashRegister;

import com.aurealab.dto.CashRegister.CashMovementProductsDTO;
import com.aurealab.dto.CashRegister.response.CashMovementResponseDTO;
import com.aurealab.model.cashRegister.entity.CashMovementProductsEntity;

public class CashMovementProductMapper {

    private CashMovementProductMapper() {
    }


    /* ===================== Entity -> DTO ===================== */
    public static CashMovementProductsDTO toDto(CashMovementProductsEntity entity) {
        if (entity == null) return null;

        return new CashMovementProductsDTO(
                entity.getId(),
                entity.getProduct(),
                entity.getQuantity(),
                entity.getUnitPrice(),
                entity.getSubtotal(),
                CashMovementMapper.toDto(entity.getCashMovement())
        );
    }

    /* ===================== DTO -> Entity ===================== */
    public static CashMovementProductsEntity toEntity(
            CashMovementProductsDTO dto
    ) {
        if (dto == null) return null;

        CashMovementProductsEntity entity = new CashMovementProductsEntity();
        entity.setId(dto.id());
        entity.setProduct(dto.product());
        entity.setQuantity(dto.quantity());
        entity.setUnitPrice(dto.unitPrice());

        return entity;
    }
}
