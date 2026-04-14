package com.aurealab.dto;

import com.aurealab.dto.CashRegister.ProductDTO;
import com.aurealab.model.inventory.entity.ProductEntity;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

@Builder
public record PurchasingItemDTO(
        Long id,
        Long purchasingId, // Solo el ID para evitar recursión infinita
        ProductDTO product,
        BatchDTO batch,
        PrescriptionInventoryDTO inventory, // Usamos el DTO de inventario que ya creamos
        BigDecimal priceUnit,
        int units,
        BigDecimal priceTotal,
        PurchasingDTO purchasing,
        LocalDate expirationDate,
        BigDecimal sellPrice
) {
}
