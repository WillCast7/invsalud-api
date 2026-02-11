package com.aurealab.dto.CashRegister.request;

import com.aurealab.dto.CashRegister.ThirdPartyRoleDTO;

import java.time.OffsetDateTime;
import java.util.Set;

public record ThirdPartyDTO(
        Long id,
        String dniType,
        String dniNumber,
        String fullName,
        String email,
        String address,
        String phoneNumber,
        OffsetDateTime createdAt,
        Long createdBySystemUserId,
        Set<ThirdPartyRoleDTO> roles) {
}
