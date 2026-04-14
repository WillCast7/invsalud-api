package com.aurealab.mapper;


import com.aurealab.dto.PermisionDTO;
import com.aurealab.model.aurea.entity.PermissionEntity;

public class PermissionMapper {
    private PermissionMapper() {
    }

    /* ===================== Entity -> DTO ===================== */
    public static PermisionDTO toDto(PermissionEntity entity) {
        if (entity == null) return null;

        return new PermisionDTO(
                entity.getId(),
                entity.getName()
        );
    }

    /* ===================== DTO -> Entity ===================== */
    public static PermissionEntity toEntity(PermisionDTO dto) {
        if (dto == null) return null;

        PermissionEntity entity = new PermissionEntity();
        entity.setId(dto.id());
        entity.setName(dto.name());
        return entity;
    }
}
