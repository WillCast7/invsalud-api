package com.aurealab.dto.CashRegister.response;

import com.aurealab.dto.CashRegister.CashSessionDTO;

import java.util.List;
import java.util.Set;

public record TransactionListSessionResponseDTO(
        List<CashMovementResponseDTO> cashMovement,
        CashSessionDTO cashSession
) {
}
