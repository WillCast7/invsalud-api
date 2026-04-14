package com.aurealab.dto.CashRegister.request;

import com.aurealab.dto.CashRegister.CashSessionDTO;
import com.aurealab.dto.CashRegister.ChargeDTO;
import com.aurealab.dto.CashRegister.PaymentMethodDTO;
import com.aurealab.dto.CashRegister.ThirdPartyDTO;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Set;

public record CashMovementRequestDTO(
        Long paymentMethodId,
        BigDecimal receivedAmount,
        String concept,
        String observations,
        String product,
        BigDecimal expectedAmount,
        Long cashSessionId,
        int advisorId, //Advisor
        String referenceNumber
){
}
