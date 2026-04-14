package com.aurealab.mapper.inventory;

import com.aurealab.dto.CashRegister.PaymentMethodDTO;
import com.aurealab.model.inventory.entity.PaymentMethodEntity;

public class PaymentMethodMapper {

    private PaymentMethodMapper() {
    }

    /* ===================== Entity -> DTO ===================== */
    public static PaymentMethodDTO toDto(PaymentMethodEntity entity) {
        if (entity == null) return null;

        return new PaymentMethodDTO(
                entity.getId(),
                entity.getCode(),
                entity.getName(),
                entity.getDescription(),
                entity.isActive(),
                entity.getOrder()
        );
    }

    /* ===================== DTO -> Entity ===================== */
    public static PaymentMethodEntity toEntity(PaymentMethodDTO dto) {
        if (dto == null) return null;

        PaymentMethodEntity entity = new PaymentMethodEntity();
        entity.setId(dto.id());
        entity.setCode(dto.code());
        entity.setName(dto.name());
        entity.setDescription(dto.description());
        entity.setActive(dto.isActive());
        entity.setOrder(dto.order());

        return entity;
    }
}
