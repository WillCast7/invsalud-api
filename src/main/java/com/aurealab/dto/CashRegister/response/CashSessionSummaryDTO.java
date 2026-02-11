package com.aurealab.dto.CashRegister.response;

import java.math.BigDecimal;

public record CashSessionSummaryDTO(
    BigDecimal totalIncome,
    BigDecimal totalExpense,
    BigDecimal netBalance
){}
