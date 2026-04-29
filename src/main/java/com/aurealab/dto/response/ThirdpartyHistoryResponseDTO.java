package com.aurealab.dto.response;

import com.aurealab.dto.CashRegister.ThirdPartyDTO;
import com.aurealab.dto.CashRegister.response.CashMovementResponseDTO;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ThirdpartyHistoryResponseDTO(
        ThirdPartyDTO thirdparty,
        @JsonProperty("movements")
        List<CashMovementResponseDTO> cashMovements
) {
}
