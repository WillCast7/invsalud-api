package com.aurealab.mapper;

import com.aurealab.dto.CashRegister.ChargeDTO;
import com.aurealab.dto.CashRegister.ChargeProductDTO;
import com.aurealab.model.cashRegister.entity.ChargeEntity;
import com.aurealab.model.cashRegister.entity.ChargeProductEntity;
import com.aurealab.model.cashRegister.entity.ThirdPartyEntity;

import java.util.List;

public class ChargeMapper {

    private ChargeMapper() {
    }

    /* ===================== Entity -> DTO ===================== */
    public static ChargeDTO toDto(ChargeEntity entity) {
        if (entity == null) return null;

        List<ChargeProductDTO> items = entity.getItems() == null
                ? List.of()
                : entity.getItems().stream()
                .map(item -> new ChargeProductDTO(
                        item.getId(),
                        item.getProductEntity() != null
                                ? item.getProductEntity().getId()
                                : null,
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getSubtotal()
                ))
                .toList();

        return new ChargeDTO(
                entity.getId(),
                entity.getPerson() != null ? entity.getPerson().getId() : null,
                entity.getTotalAmount(),
                entity.getStatus(),
                entity.getDescription(),
                items
        );
    }

    /* ===================== DTO -> Entity ===================== */
    public static ChargeEntity toEntity(
            ChargeDTO dto,
            ThirdPartyEntity person,
            List<ChargeProductEntity> items
    ) {
        if (dto == null) return null;

        ChargeEntity entity = new ChargeEntity();
        entity.setId(dto.id());
        entity.setPerson(person);
        entity.setTotalAmount(dto.totalAmount());
        entity.setStatus(dto.status());
        entity.setDescription(dto.description());
        entity.setItems(items);

        return entity;
    }
}
