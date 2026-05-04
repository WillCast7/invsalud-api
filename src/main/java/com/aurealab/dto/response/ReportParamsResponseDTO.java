package com.aurealab.dto.response;

import com.aurealab.dto.CashRegister.ProductDTO;
import com.aurealab.dto.CashRegister.ThirdPartyDTO;
import lombok.Builder;

import java.util.Set;

@Builder
public record ReportParamsResponseDTO(
        Set<ThirdPartyDTO> providers,
        Set<ProductDTO> products
) {
}
