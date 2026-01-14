package com.aurealab.mapper;

import com.aurealab.dto.CashRegister.CashSessionDTO;
import com.aurealab.model.cashRegister.entity.CashSessionEntity;

public class CashSessionMapper {
    private CashSessionMapper() {
    }

    /* ===================== Entity -> DTO ===================== */
    public static CashSessionDTO toDto(CashSessionEntity entity) {
        if (entity == null) return null;

        return new CashSessionDTO(
                entity.getId(),
                entity.getOpenedAt(),
                entity.getClosedAt(),
                entity.getOpeningAmount(),
                entity.getClosingAmount(),
                entity.getOpenedBySystemUserId(),
                entity.getClosedBySystemUserId(),
                entity.getStatus()
        );
    }

    /* ===================== DTO -> Entity ===================== */
    public static CashSessionEntity toEntity(CashSessionDTO dto) {
        if (dto == null) return null;

        CashSessionEntity entity = new CashSessionEntity();
        entity.setId(dto.id());
        entity.setOpenedAt(dto.openedAt());
        entity.setClosedAt(dto.closedAt());
        entity.setOpeningAmount(dto.openingAmount());
        entity.setClosingAmount(dto.closingAmount());
        entity.setOpenedBySystemUserId(dto.openedBySystemUserId());
        entity.setClosedBySystemUserId(dto.closedBySystemUserId());
        entity.setStatus(dto.status());

        return entity;
    }
}
