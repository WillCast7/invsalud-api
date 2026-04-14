package com.aurealab.mapper;


import com.aurealab.dto.ConfigParamDTO;
import com.aurealab.model.management.entity.ConfigParamsEntity;

public class ConfigParamsMapper {
    private ConfigParamsMapper(){}

    /* ===================== Entity -> DTO ===================== */
    public static ConfigParamDTO toDto(ConfigParamsEntity entity) {
        if (entity == null) return null;

        return new ConfigParamDTO(
                entity.getId(),
                entity.getName(),
                entity.getShortname(),
                entity.getParent(),
                entity.getDefinition(),
                entity.getOrder(),
                entity.isActive()
        );
    }

    /* ===================== DTO -> Entity ===================== */
    public static ConfigParamsEntity toEntity(ConfigParamDTO dto) {
        if (dto == null) return null;

        ConfigParamsEntity entity = new ConfigParamsEntity();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setShortname(dto.getShortname());
        entity.setParent(dto.getParent());
        entity.setDefinition(dto.getDefinition());
        entity.setOrder(dto.getOrder());
        entity.setActive(dto.isActive());
        return entity;
    }
}
