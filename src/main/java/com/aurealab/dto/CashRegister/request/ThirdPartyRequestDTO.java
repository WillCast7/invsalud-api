package com.aurealab.dto.CashRegister.request;

import com.aurealab.dto.CashRegister.ThirdPartyRoleDTO;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

public record ThirdPartyRequestDTO(
        Long id,
        String documentType,
        String documentNumber,
        String fullName,
        String email,
        String address,
        String phoneNumber,
        OffsetDateTime createdAt,
        Long createdBySystemUserId,
        List<Long> rolesIds,
        Set<ThirdPartyRoleDTO> roles) {
}
