package com.aurealab.mapper;

import com.aurealab.dto.CashRegister.ThirdPartyDTO;
import com.aurealab.model.cashRegister.entity.ThirdPartyEntity;
import com.aurealab.model.cashRegister.entity.ThirdPartyRoleEntity;

import java.util.Set;
import java.util.stream.Collectors;

public class ThirdPartyMapper {

    private ThirdPartyMapper() {
    }

    /* ===================== Entity -> DTO ===================== */
    public static ThirdPartyDTO toDto(ThirdPartyEntity entity) {
        if (entity == null) return null;

        Set<Long> roleIds = entity.getRoleEntities() == null
                ? Set.of()
                : entity.getRoleEntities()
                .stream()
                .map(ThirdPartyRoleEntity::getId)
                .collect(Collectors.toSet());

        return new ThirdPartyDTO(
                entity.getId(),
                entity.getDniType(),
                entity.getDniNumber(),
                entity.getFullName(),
                entity.getEmail(),
                entity.getAddress(),
                entity.getCreatedAt(),
                entity.getCreatedBySystemUserId(),
                roleIds
        );
    }

    /* ===================== DTO -> Entity ===================== */
    public static ThirdPartyEntity toEntity(
            ThirdPartyDTO dto,
            Set<ThirdPartyRoleEntity> roles
    ) {
        if (dto == null) return null;

        ThirdPartyEntity entity = new ThirdPartyEntity();
        entity.setId(dto.id());
        entity.setDniType(dto.dniType());
        entity.setDniNumber(dto.dniNumber());
        entity.setFullName(dto.fullName());
        entity.setEmail(dto.email());
        entity.setAddress(dto.address());
        entity.setCreatedAt(dto.createdAt());
        entity.setCreatedBySystemUserId(dto.createdBySystemUserId());
        entity.setRoleEntities(roles);

        return entity;
    }
}
