package com.aurealab.mapper.CashRegister;

import com.aurealab.dto.CashRegister.FollowingDTO;
import com.aurealab.dto.CashRegister.FollowingItemDTO;
import com.aurealab.model.cashRegister.entity.FollowingEntity;
import com.aurealab.model.cashRegister.entity.FollowingItemEntity;

import java.util.HashSet;
import java.util.Set;

public class FollowingMapper {

    private FollowingMapper() {
    }

    /* ===================== Entity -> DTO ===================== */
    public static FollowingDTO toDto(FollowingEntity entity) {
        if (entity == null) return null;

        Set<FollowingItemDTO> items = new HashSet<>();
        if (entity.getItems() != null) {
            entity.getItems().forEach(item -> items.add(FollowingItemMapper.toDto(item)));
        }

        return new FollowingDTO(
                entity.getId(),
                entity.getCreatedAt(),
                entity.getCreatedBy(),
                entity.getName(),
                entity.isActive(),
                items
        );
    }

    /* ===================== DTO -> Entity ===================== */
    public static FollowingEntity toEntity(FollowingDTO dto, Long userId) {
        if (dto == null) return null;

        FollowingEntity entity = new FollowingEntity();
        entity.setId(dto.id());
        entity.setCreatedAt(dto.createdAt());
        entity.setCreatedBy((userId == null) ? dto.createdBy() : userId);
        entity.setName(dto.name());
        entity.setActive(dto.isActive());

        if (dto.items() != null) {
            Set<FollowingItemEntity> items = new HashSet<>();
            dto.items().forEach(item -> {
                FollowingItemEntity itemEntity = FollowingItemMapper.toEntity(item);
                if (itemEntity != null) {
                    itemEntity.setFollowing(entity); // Set relationship
                    items.add(itemEntity);
                }
            });
            entity.setItems(items);
        }

        return entity;
    }
}
