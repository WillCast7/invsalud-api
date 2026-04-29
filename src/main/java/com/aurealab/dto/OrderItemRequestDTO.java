package com.aurealab.dto;

import java.math.BigDecimal;

public record OrderItemRequestDTO(
        Long product,
        BigDecimal priceUnit,
        Long units,
        BigDecimal priceTotal
) {
}
