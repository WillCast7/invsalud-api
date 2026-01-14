package com.aurealab.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class RoleDTO {
    private Long rolId;
    private String role;
    private String rolDescription;
    private String roleName;
    private boolean status;
}
