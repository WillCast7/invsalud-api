package com.aurealab.dto.CashRegister.request;

import com.aurealab.dto.CashRegister.ThirdPartyDTO;

public record SaveTransactionDTO(
        ThirdPartyDTO customer,
        CashMovementRequestDTO transaction
) {
}
