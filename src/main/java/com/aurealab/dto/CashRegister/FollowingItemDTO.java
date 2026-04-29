package com.aurealab.dto.CashRegister;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record FollowingItemDTO(
        Long id,
        ProductDTO product,
        String itemName,
        BigDecimal itemPrice,
        String itemType,
        String status,
        LocalDateTime statusDoneAt,
        ThirdPartyDTO provider
) {
}
