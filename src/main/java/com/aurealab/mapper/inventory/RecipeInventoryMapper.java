package com.aurealab.mapper.inventory;

import com.aurealab.dto.RecipeInventoryDTO;
import com.aurealab.model.inventory.entity.RecipeInventoryEntity;

public class RecipeInventoryMapper {
    private RecipeInventoryMapper() {
        // Constructor privado para evitar instanciación
    }

    /* ===================== Entity -> DTO ===================== */
    public static RecipeInventoryDTO toDto(RecipeInventoryEntity entity) {
        if (entity == null) return null;

        return new RecipeInventoryDTO(
                entity.getId(),
                entity.getPrice(),
                entity.getAvaliableUnits(),
                entity.getTotalUnits()
        );
    }

}
