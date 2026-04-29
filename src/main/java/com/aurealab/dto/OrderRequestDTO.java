package com.aurealab.dto;

import java.math.BigDecimal;
import java.util.List;

public record OrderRequestDTO(
        Long thirdParty,
        String type,
        BigDecimal total,
        List<OrderItemRequestDTO> items
) {
}
