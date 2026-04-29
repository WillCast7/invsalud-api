package com.aurealab.mapper.CashRegister;

import com.aurealab.dto.CashRegister.FollowingItemDTO;
import com.aurealab.model.cashRegister.entity.FollowingItemEntity;

public class FollowingItemMapper {

    private FollowingItemMapper() {
    }

    /* ===================== Entity -> DTO ===================== */
    public static FollowingItemDTO toDto(FollowingItemEntity entity) {
        if (entity == null) return null;

        return new FollowingItemDTO(
                entity.getId(),
                ProductMapper.toDto(entity.getProduct()),
                entity.getItemName(),
                entity.getItemPrice(),
                entity.getItemType(),
                entity.getStatus(),
                entity.getStatusDoneAt(),
                ThirdPartyMapper.toDto(entity.getProvider())
        );
    }

    /* ===================== DTO -> Entity ===================== */
    public static FollowingItemEntity toEntity(FollowingItemDTO dto) {
        if (dto == null) return null;

        FollowingItemEntity entity = new FollowingItemEntity();
        entity.setId(dto.id());
        entity.setProduct(ProductMapper.toEntity(dto.product()));
        entity.setItemName(dto.itemName());
        entity.setItemPrice(dto.itemPrice());
        entity.setItemType(dto.itemType());
        entity.setStatus(dto.status());
        entity.setStatusDoneAt(dto.statusDoneAt());
        entity.setProvider(ThirdPartyMapper.toEntity(dto.provider()));

        return entity;
    }
}
