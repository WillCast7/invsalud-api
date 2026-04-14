package com.aurealab.dto.CashRegister.request;

import com.aurealab.dto.CashRegister.ThirdPartyDTO;
import com.aurealab.dto.PurchasingItemDTO;
import com.aurealab.dto.PurchasingRecipeDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record PurchasingRequestDTO(
    Long id,
    Long thirdParty,
    BigDecimal total,
    String type,
    String observations,
    LocalDateTime createdAt,
    Long createdBy,
    String purchasedBy,
    String purchasedCode,
    Boolean isActive,
    List<PurchasingItemDTO> items,
    PurchasingRecipeDTO recipe
    ) {}
