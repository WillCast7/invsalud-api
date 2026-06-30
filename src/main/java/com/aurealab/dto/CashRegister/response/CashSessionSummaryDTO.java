package com.aurealab.dto.CashRegister.response;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record CashSessionSummaryDTO(
    BigDecimal initialAmount,
    BigDecimal totalIncome,
    BigDecimal totalExpense,
    BigDecimal netBalance,
    BigDecimal netCashBalance,

    // Compras (PurchasingEntity)
    BigDecimal purchasesMedicines,
    BigDecimal purchasesMedicinesSp,
    BigDecimal purchasesRecipes,

    // Cotizaciones (OrderEntity where isSold = false)
    BigDecimal quotesMedicines,
    BigDecimal quotesMedicinesSp,
    BigDecimal quotesRecipes,

    // Ventas (OrderEntity where isSold = true)
    BigDecimal salesMedicines,
    BigDecimal salesMedicinesSp,
    BigDecimal salesRecipes
){}
