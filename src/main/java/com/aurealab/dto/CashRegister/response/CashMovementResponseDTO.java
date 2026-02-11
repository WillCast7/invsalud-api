package com.aurealab.dto.CashRegister.response;

import com.aurealab.dto.CashRegister.CashSessionDTO;
import com.aurealab.dto.CashRegister.ChargeDTO;
import com.aurealab.dto.CashRegister.PaymentMethodDTO;
import com.aurealab.dto.CashRegister.ThirdPartyDTO;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record CashMovementResponseDTO(
        Long id,
        CashSessionDTO cashSession,
        ChargeDTO charge,
        ThirdPartyDTO customer,
        String type,
        BigDecimal expectedAmount,
        BigDecimal receivedAmount,
        String concept,
        String product,
        PaymentMethodDTO paymentMethod,
        int advisor,
        boolean isVoid,
        OffsetDateTime createdAt,
        Long createdBySystemUserId
){
}
