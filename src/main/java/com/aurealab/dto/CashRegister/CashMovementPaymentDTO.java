package com.aurealab.dto.CashRegister;

import java.math.BigDecimal;

public record CashMovementPaymentDTO(
        Long id,
        PaymentMethodDTO paymentMethod,
        BigDecimal amount,
        String transactionReference
) {
}
