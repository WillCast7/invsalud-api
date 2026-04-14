package com.aurealab.dto.response;

import com.aurealab.dto.ConfigParamDTO;
import com.aurealab.dto.RoleDTO;
import com.aurealab.dto.UserDTO;
import lombok.Builder;

import java.util.Set;

@Builder
public record UserWithParamsResponseDTO(
        UserDTO user,
        Set<ConfigParamDTO> documentTypes,
        Set<RoleDTO> roles
) {}
