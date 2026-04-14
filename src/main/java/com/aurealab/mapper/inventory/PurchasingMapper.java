package com.aurealab.mapper.inventory;

import com.aurealab.dto.PurchasingDTO;
import com.aurealab.dto.PurchasingItemDTO;
import com.aurealab.dto.tables.PurchasingTableDTO;
import com.aurealab.model.inventory.entity.PurchasingEntity;
import com.aurealab.model.inventory.entity.PurchasingItemEntity;

import java.util.ArrayList;
import java.util.List;

public class PurchasingMapper {
    private PurchasingMapper() {
        // Constructor privado para evitar instanciación
    }

    /* ===================== Entity -> DTO ===================== */
    public static PurchasingDTO toDto(PurchasingEntity entity) {
        if (entity == null) return null;

        // 1. Mapear ítems
        List<PurchasingItemDTO> items = new ArrayList<>();
        if (entity.getItems() != null) {
            entity.getItems().forEach(item -> items.add(PurchasingItemMapper.toDto(item)));
        }

        return new PurchasingDTO(
                entity.getId(),
                // 2. Usar toDtoList para evitar cargar Roles (Lazy) que no necesitas aquí
                ThirdPartyMapper.toDtoList(entity.getThirdParty()),
                entity.getTotal(),
                entity.getType(),
                entity.getObservations(),
                entity.getCreatedAt(),
                entity.getCreatedBy(),
                entity.getPurchasedBy(),
                entity.getPurchasedCode(),
                entity.getIsActive(),
                items
        );
    }

    /* ===================== Entity -> DTO ===================== */
    public static PurchasingTableDTO toTableDto(PurchasingEntity entity) {
        if (entity == null) return null;

        return new PurchasingTableDTO(
                entity.getId(),
                entity.getThirdParty().getFullName(),
                entity.getTotal(),
                entity.getType(),
                entity.getCreatedAt(),
                entity.getPurchasedBy(),
                entity.getPurchasedCode(),
                entity.getIsActive()
        );
    }

    /* ===================== DTO -> Entity ===================== */
    public static PurchasingEntity toEntity(PurchasingDTO dto) {
        if (dto == null) return null;
        List<PurchasingItemEntity> items = new ArrayList<>();
        dto.items().forEach(item -> items.add(PurchasingItemMapper.toEntity(item)));

        PurchasingEntity entity = new PurchasingEntity();
        entity.setId(dto.id());
        entity.setThirdParty(ThirdPartyMapper.toEntity(dto.thirdParty()));
        entity.setTotal(dto.total());
        entity.setType(dto.type());
        entity.setObservations(dto.observations());
        entity.setCreatedBy(dto.createdBy());
        entity.setPurchasedBy(dto.purchasedBy());
        entity.setPurchasedCode(dto.purchasedCode());
        entity.setIsActive(dto.isActive());
        entity.setItems(items);

        return entity;
    }

}
