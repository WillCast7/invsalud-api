package com.aurealab.dto.CashRegister;

import java.time.LocalDateTime;
import java.util.Set;

public record FollowingDTO(
        Long id,
        LocalDateTime createdAt,
        Long createdBy,
        String name,
        boolean isActive,
        Set<FollowingItemDTO> items

) {
}
