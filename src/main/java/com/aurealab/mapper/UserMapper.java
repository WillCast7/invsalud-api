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

        return UserDTO.builder()
                .id(entity.getId())
                .email(entity.getEmail())
                .userName(entity.getUserName())
                .password(null)
                .person(PersonMapper.toDto(entity.getPerson()))
                .role(RoleMapper.toDto(entity.getRole()))
                .company(CompanyMapper.toDto(entity.getCompany()))
                .isEnable(entity.isEnable())
                .mustChangePassword(entity.getMustChangePassword())
                .build();
    }

    /* ===================== Entity -> DTO ===================== */
    public static UserDTO toDtoWithPassword(UserEntity entity) {
        if (entity == null) return null;

        return UserDTO.builder()
                .id(entity.getId())
                .email(entity.getEmail())
                .userName(entity.getUserName())
                .password(entity.getPassword())
                .person(PersonMapper.toDto(entity.getPerson()))
                .role(RoleMapper.toDto(entity.getRole()))
                .company(CompanyMapper.toDto(entity.getCompany()))
                .isEnable(entity.isEnable())
                .mustChangePassword(entity.getMustChangePassword())
                .build();
    }

    /* ===================== Entity -> DTO ===================== */
    public static UserDTO toDtoResponse(UserEntity entity) {
        if (entity == null) return null;

        return UserDTO.builder()
                .id(entity.getId())
                .email(entity.getEmail())
                .userName(entity.getUserName())
                .password(null)
                .person(PersonMapper.toDto(entity.getPerson()))
                .role(null)
                .company(CompanyMapper.toDto(entity.getCompany()))
                .isEnable(entity.isEnable())
                .mustChangePassword(entity.getMustChangePassword())
                .build();
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
        entity.setMustChangePassword(dto.getMustChangePassword() != null ? dto.getMustChangePassword() : true);

        return entity;
    }
}
