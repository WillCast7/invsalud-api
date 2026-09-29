package com.aurealab.dto.tables;

import com.aurealab.model.inventory.entity.OrderItemEntity;
import com.aurealab.model.inventory.entity.ThirdPartyEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OrderTableDTO(
        Long id,
        String thirdParty,
        BigDecimal total,
        String status,
        String observations,
        String orderCode,
        String soldCode,
        LocalDateTime createdAt,
        LocalDateTime expirateAt,
        LocalDateTime soldAt,
        boolean isActive,
        boolean isSold,
        String type,
        UUID quoteTemplateOrderId,
        UUID quoteTemplateSoldId
) {
}
