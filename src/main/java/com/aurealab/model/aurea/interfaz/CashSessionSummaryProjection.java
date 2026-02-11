package com.aurealab.model.aurea.interfaz;

import java.math.BigDecimal;

public interface CashSessionSummaryProjection {
    BigDecimal getTotalIncome();
    BigDecimal getTotalExpense();
    BigDecimal getNetBalance();
}
