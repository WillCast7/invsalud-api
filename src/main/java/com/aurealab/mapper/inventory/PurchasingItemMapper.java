package com.aurealab.mapper.inventory;

import com.aurealab.dto.PurchasingItemDTO;
import com.aurealab.model.inventory.entity.PurchasingEntity;
import com.aurealab.model.inventory.entity.PurchasingItemEntity;

public class PurchasingItemMapper {

    private PurchasingItemMapper() {
        // Constructor privado para evitar instanciación
    }

    public static PurchasingItemDTO toDto(PurchasingItemEntity entity) {
        if (entity == null) {
            return null;
        }

System.out.println("is purchasing mapper");

        return PurchasingItemDTO.builder()
                .id(entity.getId())
                .batch(BatchMapper.toDto(entity.getBatch()))
                .product(ProductMapper.toDto(entity.getProduct()))
                .sellPrice(entity.getSellPrice())
                .expirationDate(entity.getExpirationDate())
                .priceUnit(entity.getPriceUnit())
                .units(entity.getUnits())
                .priceTotal(entity.getPriceTotal())
                .build();
    }

    public static PurchasingItemEntity toEntity(PurchasingItemDTO dto) {
        if (dto == null) return null;

        PurchasingItemEntity entity = new PurchasingItemEntity();
        entity.setId(dto.id());

        // IMPORTANTE: No llames a PurchasingMapper.toEntity(dto.purchasing()) aquí
        // La relación se establece en el Service con item.setPurchasing(parent)

        entity.setBatch(BatchMapper.toEntity(dto.batch()));
        entity.setProduct(ProductMapper.toEntity(dto.product()));
        entity.setSellPrice(dto.sellPrice());
        entity.setPriceUnit(dto.priceUnit());
        entity.setExpirationDate(dto.expirationDate());
        entity.setUnits(dto.units());
        entity.setPriceTotal(dto.priceTotal());

        return entity;
    }
}
