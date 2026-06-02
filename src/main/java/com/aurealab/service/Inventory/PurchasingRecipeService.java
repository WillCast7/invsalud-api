package com.aurealab.service.Inventory;

import com.aurealab.dto.PrescriptionInventoryTableDTO;
import com.aurealab.model.inventory.entity.PurchasingRecipeEntity;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;

public interface PurchasingRecipeService {
    public PurchasingRecipeEntity savePurchasingEntity(PurchasingRecipeEntity purchasingRecipeEntity);
    public Page<PrescriptionInventoryTableDTO> getPurchasingRecipeReport(
            int page, int size, LocalDateTime start, LocalDateTime end,
            String documentNumber, String product);
}
