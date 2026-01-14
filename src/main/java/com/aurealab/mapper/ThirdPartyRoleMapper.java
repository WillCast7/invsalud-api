package com.aurealab.mapper;

import com.aurealab.dto.CashRegister.ThirdPartyRoleDTO;
import com.aurealab.model.cashRegister.entity.ThirdPartyRoleEntity;

public class ThirdPartyRoleMapper {
    private ThirdPartyRoleMapper() {
    }

    /* ===================== Entity -> DTO ===================== */
    public static ThirdPartyRoleDTO toDto(ThirdPartyRoleEntity entity) {
        if (entity == null) return null;

        return new ThirdPartyRoleDTO(
                entity.getId(),
                entity.getRoleName()
        );
    }

    /* ===================== DTO -> Entity ===================== */
    public static ThirdPartyRoleEntity toEntity(ThirdPartyRoleDTO dto) {
        if (dto == null) return null;

        ThirdPartyRoleEntity entity = new ThirdPartyRoleEntity();
        entity.setId(dto.id());
        entity.setRoleName(dto.roleName());

        return entity;
    }

}
