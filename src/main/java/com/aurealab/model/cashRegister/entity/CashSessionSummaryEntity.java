package com.aurealab.model.cashRegister.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CashSessionSummaryEntity{
    BigDecimal totalIncome;
    BigDecimal totalExpense;
    BigDecimal netBalance;
}
