package com.aurealab.dto.CashRegister;

public record PaymentMethodDTO(
    Long id,
    String code,
    String name,
    String description,
    boolean isActive,
    int order
) {}
