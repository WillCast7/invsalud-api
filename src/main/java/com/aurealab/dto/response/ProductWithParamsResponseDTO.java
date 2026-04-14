package com.aurealab.dto.response;

import com.aurealab.dto.CashRegister.ProductDTO;
import com.aurealab.dto.ConfigParamDTO;
import lombok.Builder;

import java.util.Set;

@Builder
public record ProductWithParamsResponseDTO(
        ProductDTO product,
        Set<ConfigParamDTO> configParams
) {
}
