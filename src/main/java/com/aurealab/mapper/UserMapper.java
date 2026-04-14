package com.aurealab.mapper;


import com.aurealab.dto.UserDTO;
import com.aurealab.dto.response.UserTableResponseDTO;
import com.aurealab.model.aurea.entity.UserEntity;
import com.aurealab.service.UserService;
import com.aurealab.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;

public class UserMapper {

    @Autowired
    static UserService userService;

    @Autowired
    static JwtUtils jwtUtils;

    private UserMapper() {
    }

    /* ===================== Entity -> DTO ===================== */
    public static UserDTO toDto(UserEntity entity) {
        if (entity == null) return null;

        return new UserDTO(
                entity.getId(),
                entity.getEmail(),
                entity.getUserName(),
                null,
                PersonMapper.toDto(entity.getPerson()),
                RoleMapper.toDto(entity.getRole()),
                CompanyMapper.toDto(entity.getCompany()),
                entity.isEnable()
        );
    }

    /* ===================== Entity -> DTO ===================== */
    public static UserDTO toDtoWithPassword(UserEntity entity) {
        if (entity == null) return null;

        return new UserDTO(
                entity.getId(),
                entity.getEmail(),
                entity.getUserName(),
                entity.getPassword(),
                PersonMapper.toDto(entity.getPerson()),
                RoleMapper.toDto(entity.getRole()),
                CompanyMapper.toDto(entity.getCompany()),
                entity.isEnable()
        );
    }

    /* ===================== Entity -> DTO ===================== */
    public static UserDTO toDtoResponse(UserEntity entity) {
        if (entity == null) return null;

        return new UserDTO(
                entity.getId(),
                entity.getEmail(),
                entity.getUserName(),
                null,
                PersonMapper.toDto(entity.getPerson()),
                null,
                CompanyMapper.toDto(entity.getCompany()),
                entity.isEnable()
        );
    }

    public static UserTableResponseDTO toDtoSimplyResponse(UserEntity entity) {
        if (entity == null) return null;

        return UserTableResponseDTO.builder()
                .id(entity.getId())
                .email(entity.getEmail())
                .userName(entity.getUserName())
                .documentType(entity.getPerson().getDocumentType())
                .documentNumber(entity.getPerson().getDocumentNumber())
                .fullName(entity.getPerson().getNames() + " " + entity.getPerson().getSurnames())
                .phoneNumber(entity.getPerson().getPhoneNumber())
                .address(entity.getPerson().getAddress())
                .birthDate(entity.getPerson().getBirthDate())
                .build();
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
        entity.setPassword("$2a$10$U48O/d.mXdpH2IGU8tqCLO/z0/VrylEOQqB66mkJi1S2GqXwiAR/G");

        return entity;
    }
}
