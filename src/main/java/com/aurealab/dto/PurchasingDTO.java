package com.aurealab.dto;

import com.aurealab.dto.CashRegister.ThirdPartyDTO;
import com.aurealab.model.inventory.entity.PurchasingItemEntity;
import com.aurealab.model.inventory.entity.ThirdPartyEntity;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record PurchasingDTO(
    Long id,
    ThirdPartyDTO thirdParty,
    BigDecimal total,
    String type,
    String observations,
    LocalDateTime createdAt,
    Long createdBy,
    String purchasedBy,
    String purchasedCode,
    Boolean isActive,
    List<PurchasingItemDTO> items
    ) {}
