package com.aurealab.dto.CashRegister.response;

import com.aurealab.dto.CashRegister.ThirdPartyDTO;

import java.util.Set;

public record ThirdpartyHistoryResponseDTO(
        ThirdPartyDTO thirdParty,
        Set<CashMovementResponseDTO> cashMovements
) {
}
