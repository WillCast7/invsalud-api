package com.aurealab.dto.CashRegister;

import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.Set;

@Builder
public record ThirdPartyDTO(
        Long id,
        String documentType,
        String documentNumber,
        String fullName,
        String email,
        String address,
        String phoneNumber,
        OffsetDateTime createdAt,
        Long createdBySystemUserId,
        Set<ThirdPartyRoleDTO> roles) {
}
