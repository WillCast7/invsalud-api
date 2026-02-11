package com.aurealab.mapper.CashRegister;

import com.aurealab.dto.CashRegister.ThirdPartyDTO;
import com.aurealab.dto.CashRegister.ThirdPartyRoleDTO;
import com.aurealab.model.cashRegister.entity.ThirdPartyEntity;
import com.aurealab.model.cashRegister.entity.TPRoleEntity;

import java.util.HashSet;
import java.util.Set;

public class ThirdPartyMapper {

    private ThirdPartyMapper() {
    }

    /* ===================== Entity -> DTO ===================== */
    public static ThirdPartyDTO toDto(ThirdPartyEntity entity) {
        if (entity == null) return null;

        Set<ThirdPartyRoleDTO> roles = new HashSet<>();

        entity.getRoles().forEach(tpRoleEntity ->
                roles.add(ThirdPartyRoleMapper.toDto(tpRoleEntity)));

        return new ThirdPartyDTO(
                entity.getId(),
                entity.getDocumentType(),
                entity.getDocumentNumber(),
                entity.getFullName(),
                entity.getEmail(),
                entity.getAddress(),
                entity.getPhoneNumber(),
                entity.getCreatedAt(),
                entity.getCreatedBySystemUserId(),
                roles
        );
    }

    /* ===================== Entity -> DTO ===================== */
    public static ThirdPartyDTO toDtoList(ThirdPartyEntity entity) {
        if (entity == null) return null;



        return new ThirdPartyDTO(
                entity.getId(),
                entity.getDocumentType(),
                entity.getDocumentNumber(),
                entity.getFullName(),
                entity.getEmail(),
                entity.getAddress(),
                entity.getPhoneNumber(),
                entity.getCreatedAt(),
                entity.getCreatedBySystemUserId(),
                null
        );
    }


    /* ===================== DTO -> Entity ===================== */
    public static ThirdPartyEntity toEntity(
            ThirdPartyDTO dto
    ) {
        if (dto == null) return null;

        Set<TPRoleEntity> roles = new HashSet<>();
        dto.roles().forEach(tpRoleDTO ->
        roles.add(ThirdPartyRoleMapper.toEntity(tpRoleDTO)));

        ThirdPartyEntity entity = new ThirdPartyEntity();
        entity.setId(dto.id());
        entity.setDocumentType(dto.documentType());
        entity.setDocumentNumber(dto.documentNumber());
        entity.setFullName(dto.fullName());
        entity.setEmail(dto.email());
        entity.setAddress(dto.address());
        entity.setPhoneNumber(dto.phoneNumber());
        entity.setCreatedAt(dto.createdAt());
        entity.setCreatedBySystemUserId(dto.createdBySystemUserId());

        entity.setRoles(roles);

        return entity;
    }
}
