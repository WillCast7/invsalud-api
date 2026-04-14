package com.aurealab.mapper.inventory;

import com.aurealab.dto.BatchDTO;
import com.aurealab.model.inventory.entity.BatchEntity;

public class BatchMapper {

    private BatchMapper() {
    }

    /* ===================== Entity -> DTO ===================== */
    public static BatchDTO toDto(BatchEntity entity) {
        if (entity == null) return null;

        return new BatchDTO(
                entity.getId(),
                entity.getCode(),
                entity.getIsActive()
        );
    }

    /* ===================== DTO -> Entity ===================== */
    public static BatchEntity toEntity(BatchDTO dto) {
        if (dto == null) return null;

        BatchEntity entity = new BatchEntity();
        entity.setId(dto.id());
        entity.setCode(dto.code());
        entity.setIsActive(dto.isActive());

        return entity;
    }
}
