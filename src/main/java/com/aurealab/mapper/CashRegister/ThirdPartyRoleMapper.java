package com.aurealab.mapper.CashRegister;

import com.aurealab.dto.CashRegister.ThirdPartyRoleDTO;
import com.aurealab.model.cashRegister.entity.TPRoleEntity;

public class ThirdPartyRoleMapper {
    private ThirdPartyRoleMapper() {
    }

    /* ===================== Entity -> DTO ===================== */
    public static ThirdPartyRoleDTO toDto(TPRoleEntity entity) {
        if (entity == null) return null;

        return new ThirdPartyRoleDTO(
                entity.getId(),
                entity.getRoleName(),
                entity.isActive()
        );
    }

    /* ===================== DTO -> Entity ===================== */
    public static TPRoleEntity toEntity(ThirdPartyRoleDTO dto) {
        if (dto == null) return null;

        TPRoleEntity entity = new TPRoleEntity();
        entity.setId(dto.id());
        entity.setRoleName(dto.roleName());
        entity.setActive(dto.isActive());

        return entity;
    }

}
