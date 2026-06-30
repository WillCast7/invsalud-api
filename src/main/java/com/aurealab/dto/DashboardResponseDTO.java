package com.aurealab.dto;

import com.aurealab.dto.CashRegister.response.CashSessionSummaryDTO;

import java.util.List;
import java.util.Set;

public record DashboardResponseDTO(
    Set<MenuDTO> menu,
    String logoUrl,
    List<MonthlyIncomeDTO> monthlyIncomes,
    List<PaymentMethodBreakdownDTO> paymentMethods,
    CashSessionSummaryDTO summaries
) {}
