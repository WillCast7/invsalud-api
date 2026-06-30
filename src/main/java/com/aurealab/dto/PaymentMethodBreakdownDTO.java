package com.aurealab.dto;

import java.math.BigDecimal;

public record PaymentMethodBreakdownDTO(
    String name,
    BigDecimal amount
) {}
