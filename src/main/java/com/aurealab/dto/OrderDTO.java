package com.aurealab.dto;

import com.aurealab.dto.CashRegister.ThirdPartyDTO;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OrderDTO(
        Long id,
        ThirdPartyDTO thirdParty,
        BigDecimal total,
        String status,
        String observations,
        String orderCode,
        String soldCode,
        LocalDateTime createdAt,
        LocalDateTime expirateAt,
        LocalDateTime soldAt,
        Long createdBy,
        LocalDateTime soldBy,
        boolean isActive,
        boolean isSold,
        String type,
        List<OrderItemDTO> items,
        int iva,
        BigDecimal priceIva,
        BigDecimal subtotal,
        UUID quoteTemplateOrderId,
        UUID quoteTemplateSoldId
) {
}
