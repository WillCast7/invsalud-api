package com.aurealab.mapper.CashRegister;

import com.aurealab.dto.CashRegister.ChargeDTO;
import com.aurealab.model.cashRegister.entity.ChargeEntity;
import com.aurealab.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class ChargeMapper {
    @Autowired
    JwtUtils jwtUtils;

    private ChargeMapper() {
    }

    /* ===================== Entity -> DTO ===================== */
    public static ChargeDTO toDto(ChargeEntity entity) {
        if (entity == null) return null;


        return new ChargeDTO(
                entity.getId(),
                entity.getThirdParty() != null ? ThirdPartyMapper.toDto(entity.getThirdParty()) : null,
                entity.getTotalAmount(),
                entity.getStatus(),
                entity.getDescription(),
                entity.getCreatedBySystemUserId()

        );
    }

    /* ===================== Entity -> DTO ===================== */
    public static ChargeDTO toDTOWithOutThirdParty(ChargeEntity entity) {
        if (entity == null) return null;


        return new ChargeDTO(
                entity.getId(),
                null,
                entity.getTotalAmount(),
                entity.getStatus(),
                entity.getDescription(),
                entity.getCreatedBySystemUserId()

        );
    }

    /* ===================== DTO -> Entity ===================== */
    public static ChargeEntity toEntity(
            ChargeDTO dto
    ) {
        if (dto == null) return null;
        List<ChargeEntity> items = new ArrayList<>();
        ChargeEntity entity = new ChargeEntity();
        entity.setId(dto.id());
        entity.setThirdParty(ThirdPartyMapper.toEntity(dto.thirdParty()));
        entity.setTotalAmount(dto.totalAmount());
        entity.setStatus(dto.status());
        entity.setDescription(dto.description());
        entity.setCreatedBySystemUserId(dto.setCreatedBySystemUserId());

        return entity;
    }
}
