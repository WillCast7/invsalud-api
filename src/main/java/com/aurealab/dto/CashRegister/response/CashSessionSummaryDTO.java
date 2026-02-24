package com.aurealab.dto.CashRegister.response;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record CashSessionSummaryDTO(
    BigDecimal initialAmount,
    BigDecimal totalIncome,
    BigDecimal totalExpense,
    BigDecimal netBalance,
    BigDecimal netCashBalance

){}
