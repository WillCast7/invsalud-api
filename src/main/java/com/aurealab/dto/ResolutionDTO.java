package com.aurealab.dto;

import com.aurealab.dto.CashRegister.ProductDTO;
import com.aurealab.dto.CashRegister.ThirdPartyDTO;
import com.aurealab.model.inventory.entity.ResolutionAllowedProductEntity;
import com.aurealab.model.inventory.entity.ThirdPartyEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public record ResolutionDTO(
    Long id,
    ThirdPartyDTO thirdParty,
    String code,
    LocalDate startDate,
    LocalDate expirationDate,
    String description,
    Boolean isActive,
    LocalDateTime createdAt,
    String createdBy,
    Set<ProductDTO> products

) {
}
