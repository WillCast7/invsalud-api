package com.aurealab.dto.tables;

import com.aurealab.model.inventory.entity.PurchasingItemEntity;
import com.aurealab.model.inventory.entity.ThirdPartyEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record PurchasingTableDTO(
    Long id,
    String thirdParty,
    BigDecimal total,
    String type,
    LocalDateTime createdAt,
    String purchasedBy,
    String purchasedCode,
    Boolean isActive
    ) {}
