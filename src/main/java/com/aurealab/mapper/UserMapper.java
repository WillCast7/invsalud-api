package com.aurealab.mapper;


import com.aurealab.dto.UserDTO;
import com.aurealab.model.aurea.entity.UserEntity;

public class UserMapper {
    private UserMapper() {
    }

    /* ===================== Entity -> DTO ===================== */
    public static UserDTO toDto(UserEntity entity) {
        if (entity == null) return null;

        return new UserDTO(
                entity.getId(),
                entity.getEmail(),
                entity.getUserName(),
                entity.getPassword(),
                PersonMapper.toDto(entity.getPerson()),
                RoleMapper.toDto(entity.getRole()),
                CompanyMapper.toDto(entity.getCompany())
        );
    }

    public static UserDTO toDtoSimplyResponse(UserEntity entity) {
        if (entity == null) return null;

        return new UserDTO(
                entity.getId(),
                entity.getEmail(),
                entity.getUserName(),
                entity.getPassword(),
                PersonMapper.toDto(entity.getPerson()),
                null,
                null
        );
    }

    /* ===================== DTO -> Entity ===================== */
    public static UserEntity toEntity(UserDTO dto) {
        if (dto == null) return null;

        UserEntity entity = new UserEntity();
        entity.setId(dto.getId());
        entity.setEmail(dto.getEmail());
        entity.setUserName(dto.getUserName());
        entity.setPerson(PersonMapper.toEntity(dto.getPerson()));
        entity.setRole(RoleMapper.toEntity(dto.getRole()));
        entity.setCompany(CompanyMapper.toEntity(dto.getCompany()));

        return entity;
    }
}
