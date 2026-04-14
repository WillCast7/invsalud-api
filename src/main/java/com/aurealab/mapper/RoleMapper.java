package com.aurealab.mapper;

import com.aurealab.dto.PermisionDTO;
import com.aurealab.dto.RoleDTO;
import com.aurealab.model.aurea.entity.PermissionEntity;
import com.aurealab.model.aurea.entity.RoleEntity;

import java.util.HashSet;
import java.util.Set;

public class RoleMapper {
    private RoleMapper() {
    }

    /* ===================== Entity -> DTO ===================== */
    public static RoleDTO toDto(RoleEntity entity) {
        if (entity == null) return null;

        Set<PermisionDTO> permisionList = new HashSet<>();
        entity.getPermissionList().forEach(permissionEntity ->
                permisionList.add(PermissionMapper.toDto(permissionEntity))
        );

        return new RoleDTO(
                entity.getId(),
                entity.getRole(),
                entity.getRoleDescription(),
                entity.getRoleName(),
                entity.isStatus(),
                permisionList
        );
    }

    /* ===================== DTO -> Entity ===================== */
    public static RoleEntity toEntity(RoleDTO dto) {
        if (dto == null) return null;
        Set<PermissionEntity> permissionList = new HashSet<>();
        dto.permisionList().forEach(
                permision -> permissionList.add(PermissionMapper.toEntity(permision))
        );
        RoleEntity entity = new RoleEntity();
        entity.setId(dto.id());
        entity.setRole(dto.role());
        entity.setRoleDescription(dto.roleDescription());
        entity.setRoleName(dto.roleName());
        entity.setStatus(dto.status());
        entity.setPermissionList(permissionList);

        return entity;
    }
}
