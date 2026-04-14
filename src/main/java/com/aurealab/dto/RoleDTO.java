package com.aurealab.dto;

import lombok.*;

import java.util.Set;


@Builder
public record RoleDTO(
    Long id,
    String role,
    String roleDescription,
    String roleName,
    boolean status,
    Set<PermisionDTO> permisionList
){}
