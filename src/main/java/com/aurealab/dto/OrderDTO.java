package com.aurealab.dto;

import com.aurealab.model.inventory.entity.OrderItemEntity;
import com.aurealab.model.inventory.entity.ThirdPartyEntity;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderDTO(
        Long id,
        ThirdPartyEntity thirdParty,
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
        List<OrderItemEntity> items
) {
}
