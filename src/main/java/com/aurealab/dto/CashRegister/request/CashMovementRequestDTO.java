package com.aurealab.dto.CashRegister.request;

import com.aurealab.dto.CashRegister.*;

import java.math.BigDecimal;
import java.util.Set;

public record CashMovementRequestDTO(
        Long paymentMethodId,
        BigDecimal receivedAmount,
        String concept,
        String observations,
        Set<CashMovementPaymentDTO> payments,
        BigDecimal expectedAmount,
        Long cashSessionId,
        ThirdPartyDTO advisor, //Advisor
        String referenceNumber,
        Set<ProductDTO> product,
        boolean followingIsActive,
        FollowingDTO following
){}
