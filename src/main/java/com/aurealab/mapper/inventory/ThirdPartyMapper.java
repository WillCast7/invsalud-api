package com.aurealab.mapper.inventory;

import com.aurealab.dto.CashRegister.ThirdPartyDTO;
import com.aurealab.dto.CashRegister.ThirdPartyRoleDTO;
import com.aurealab.dto.ResolutionDTO;
import com.aurealab.model.inventory.entity.ResolutionEntity;
import com.aurealab.model.inventory.entity.ThirdPartyEntity;
import com.aurealab.model.inventory.entity.TPRoleEntity;

import java.util.HashSet;
import java.util.Set;

public class ThirdPartyMapper {

    private ThirdPartyMapper() {
    }

    /* ===================== Entity -> DTO ===================== */
    public static ThirdPartyDTO toDto(ThirdPartyEntity entity) {
        if (entity == null) return null;

        Set<ThirdPartyRoleDTO> roles = new HashSet<>();
        Set<ResolutionDTO> resolutions = new HashSet<>();

        entity.getRoles().forEach(tpRoleEntity ->
                roles.add(ThirdPartyRoleMapper.toDto(tpRoleEntity)));

        entity.getResolutions().forEach(resolutionEntity ->
                resolutions.add(ResolutionMapper.toDto(resolutionEntity)));

        return new ThirdPartyDTO(
                entity.getId(),
                entity.getDocumentType(),
                entity.getDocumentNumber(),
                entity.getFullName(),
                entity.getEmail(),
                entity.getAddress(),
                entity.getPhoneNumber(),
                entity.getCreatedAt(),
                entity.getCreatedBy(),
                roles,
                resolutions
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
                entity.getCreatedBy(),
                null,
                null
        );
    }


    /* ===================== DTO -> Entity ===================== */
    public static ThirdPartyEntity toEntity(
            ThirdPartyDTO dto
    ) {
        if (dto == null) return null;

        Set<TPRoleEntity> roles = new HashSet<>();
        Set<ResolutionEntity> resolutions = new HashSet<>();

        if (dto.roles() != null) {
            dto.roles().forEach(tpRoleDTO ->
                roles.add(ThirdPartyRoleMapper.toEntity(tpRoleDTO)
                )
            );
        }

        ThirdPartyEntity entity = new ThirdPartyEntity();
        entity.setId(dto.id());
        entity.setDocumentType(dto.documentType());
        entity.setDocumentNumber(dto.documentNumber());
        entity.setFullName(dto.fullName());
        entity.setEmail(dto.email());
        entity.setAddress(dto.address());
        entity.setPhoneNumber(dto.phoneNumber());
        entity.setCreatedAt(dto.createdAt());
        entity.setCreatedBy(dto.createdBy());

        if (dto.resolutions() != null) {
            dto.resolutions().forEach(tpResolutionDTO -> {
                ResolutionEntity resEntity = ResolutionMapper.toEntity(tpResolutionDTO);
                resEntity.setThirdParty(entity);
                resolutions.add(resEntity);
            });
        }

        entity.setResolutions(resolutions);
        entity.setRoles(roles);

        return entity;
    }
}
