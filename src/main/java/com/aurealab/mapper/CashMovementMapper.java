package com.aurealab.mapper;

import com.aurealab.dto.CashRegister.CashMovementDTO;
import com.aurealab.model.cashRegister.entity.CashMovementEntity;
import com.aurealab.model.cashRegister.entity.CashSessionEntity;
import com.aurealab.model.cashRegister.entity.ChargeEntity;
import com.aurealab.model.cashRegister.entity.ThirdPartyEntity;

public class CashMovementMapper {
    private CashMovementMapper() {
    }

    /* ===================== Entity -> DTO ===================== */
    public static CashMovementDTO toDto(CashMovementEntity entity) {
        if (entity == null) return null;

        return new CashMovementDTO(
                entity.getId(),
                entity.getCashSession() != null ? CashSessionMapper.toDto(entity.getCashSession()) : null,
                entity.getCharge() != null ? ChargeMapper.toDto(entity.getCharge()) : null,
                entity.getThirdParty() != null ? ThirdPartyMapper.toDto(entity.getThirdParty()) : null,
                entity.getType(),
                entity.getAmount(),
                entity.getConcept(),
                entity.getPaymentMethod(),
                entity.getReferenceNumber(),
                entity.isVoid(),
                entity.getCreatedAt(),
                entity.getCreatedBySystemUserId()
        );
    }

    /* ===================== DTO -> Entity ===================== */
    public static CashMovementEntity toEntity(
            CashMovementDTO dto,
            CashSessionEntity session,
            ChargeEntity charge,
            ThirdPartyEntity thirdParty
    ) {
        if (dto == null) return null;

        CashMovementEntity entity = new CashMovementEntity();
        entity.setId(dto.id());
        entity.setCashSession(session);
        entity.setCharge(charge);
        entity.setThirdParty(thirdParty);
        entity.setType(dto.type());
        entity.setAmount(dto.amount());
        entity.setConcept(dto.concept());
        entity.setPaymentMethod(dto.paymentMethod());
        entity.setReferenceNumber(dto.referenceNumber());
        entity.setVoid(dto.isVoid());
        entity.setCreatedAt(dto.createdAt());
        entity.setCreatedBySystemUserId(dto.createdBySystemUserId());

        return entity;
    }
}
