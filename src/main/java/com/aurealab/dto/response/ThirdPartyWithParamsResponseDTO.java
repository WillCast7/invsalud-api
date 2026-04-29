package com.aurealab.dto.response;

import com.aurealab.dto.CashRegister.ChargeDTO;
import com.aurealab.dto.CashRegister.ProductDTO;
import com.aurealab.dto.CashRegister.CashMovementItemDTO;
import com.aurealab.dto.CashRegister.FollowingDTO;
import com.aurealab.dto.CashRegister.ThirdPartyDTO;
import com.aurealab.dto.CashRegister.ThirdPartyRoleDTO;
import com.aurealab.dto.CashRegister.request.CashMovementRequestDTO;
import com.aurealab.dto.CashRegister.response.CashMovementResponseDTO;
import com.aurealab.dto.ConfigParamDTO;
import com.aurealab.dto.RoleDTO;
import com.aurealab.dto.UserDTO;
import lombok.Builder;

import java.util.Set;

@Builder
public record ThirdPartyWithParamsResponseDTO(
        ThirdPartyDTO thirdParty,
        Set<ConfigParamDTO> documentTypes,
        Set<ThirdPartyRoleDTO> roles,
        ChargeDTO charge,
        Set<ProductDTO> products,
        Set<CashMovementResponseDTO> items,
        CashMovementResponseDTO following
) {}
