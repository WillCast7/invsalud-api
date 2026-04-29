package com.aurealab.mapper.CashRegister;

import com.aurealab.dto.CashRegister.CashMovementItemDTO;
import com.aurealab.model.cashRegister.entity.CashMovementItemsEntity;

public class CashMovementItemMapper {

    private CashMovementItemMapper() {
    }


    /* ===================== Entity -> DTO ===================== */
    public static CashMovementItemDTO toDto(CashMovementItemsEntity entity) {
        if (entity == null) return null;

        return new CashMovementItemDTO(
                entity.getId(),
                ProductMapper.toDto(entity.getProduct()),
                entity.getQuantity(),
                entity.getUnitPrice(),
                entity.getSubtotal(),
                entity.getStatusDoneAt(),
                entity.getStatus()
        );
    }

    /* ===================== DTO -> Entity ===================== */
    public static CashMovementItemsEntity toEntity(
            CashMovementItemDTO dto
    ) {
        if (dto == null) return null;

        CashMovementItemsEntity entity = new CashMovementItemsEntity();
        entity.setId(dto.id());
        entity.setProduct(ProductMapper.toEntity(dto.product()));
        entity.setQuantity(dto.quantity());
        entity.setUnitPrice(dto.unitPrice());
        entity.setStatus(dto.status());
        entity.setStatusDoneAt(dto.statusDoneAt());

        return entity;
    }
}
