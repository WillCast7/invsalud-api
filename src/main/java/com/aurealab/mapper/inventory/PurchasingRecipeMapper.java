package com.aurealab.mapper.inventory;

import com.aurealab.dto.PurchasingRecipeDTO;
import com.aurealab.model.inventory.entity.PurchasingRecipeEntity;

public class PurchasingRecipeMapper {

    public static PurchasingRecipeDTO toDTO(PurchasingRecipeEntity entity) {
        if (entity == null) {
            return null;
        }

        return new PurchasingRecipeDTO(
                entity.getId(),
                entity.getPriceUnit(),
                entity.getUnits(),
                entity.getPriceTotal(),
                entity.getStartSerial(),
                entity.getFinalSerial()
        );
    }

    public static PurchasingRecipeEntity toEntity(PurchasingRecipeDTO dto) {
        if (dto == null) {
            return null;
        }

        PurchasingRecipeEntity entity = new PurchasingRecipeEntity();
        entity.setId(dto.id());
        entity.setPriceUnit(dto.priceUnit());
        entity.setUnits(dto.units());
        entity.setPriceTotal(dto.priceTotal());
        entity.setStartSerial(dto.startSerial());
        entity.setFinalSerial(dto.finalSerial());

        return entity;
    }
}
