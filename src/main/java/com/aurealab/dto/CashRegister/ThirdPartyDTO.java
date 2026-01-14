package com.aurealab.dto.CashRegister;

import java.time.OffsetDateTime;
import java.util.Set;

public record ThirdPartyDTO(
        Long id,
        String dniType,
        String dniNumber,
        String fullName,
        String email,
        String address,
        OffsetDateTime createdAt,
        Long createdBySystemUserId,
        Set<ThirdPartyRoleDTO> roles
) {
}
