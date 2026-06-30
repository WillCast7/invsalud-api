package com.aurealab.dto;

import java.math.BigDecimal;

public record MonthlyIncomeDTO(
    String month,
    BigDecimal amount
) {}
