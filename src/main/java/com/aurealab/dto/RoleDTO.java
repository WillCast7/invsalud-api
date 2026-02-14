package com.aurealab.dto;

import lombok.*;

import java.util.Set;


@Builder
public record RoleDTO(
    Long id,
    String role,
    String rolDescription,
    String roleName,
    boolean status,
    Set<PermisionDTO> permisionList
){}
