package com.aurealab.mapper.inventory;

import com.aurealab.dto.OrderItemDTO;
import com.aurealab.dto.OrderDTO;
import com.aurealab.dto.tables.OrderTableDTO;
import com.aurealab.model.inventory.entity.OrderEntity;
import com.aurealab.model.inventory.entity.OrderItemEntity;

import java.util.ArrayList;
import java.util.List;

public class OrderMapper {
    private OrderMapper() {
        // Constructor privado para evitar instanciación
    }

    /* ===================== Entity -> DTO ===================== */
    public static OrderDTO toDto(OrderEntity entity) {
        if (entity == null) return null;

        List<OrderItemDTO> items = new ArrayList<>();
        if (entity.getItems() != null) {
            entity.getItems().forEach(item -> items.add(toItemDto(item)));
        }

        return new OrderDTO(
                entity.getId(),
                ThirdPartyMapper.toDtoList(entity.getThirdParty()), // Use toDtoList to avoid loading all resolutions if not needed
                entity.getTotal(),
                entity.getStatus(),
                entity.getObservations(),
                entity.getOrderCode(),
                entity.getSoldCode(),
                entity.getCreatedAt(),
                entity.getExpirateAt(),
                entity.getSoldAt(),
                entity.getCreatedBy(),
                entity.getSoldBy(),
                entity.isActive(),
                entity.isSold(),
                entity.getType(),
                items
        );
    }

    public static OrderItemDTO toItemDto(OrderItemEntity entity) {
        if (entity == null) return null;
        return new OrderItemDTO(
                entity.getId(),
                PrescriptionInventoryMapper.toDto(entity.getInventory()),
                entity.getPriceUnit(),
                entity.getUnits(),
                entity.getPriceTotal()
        );
    }

    /* ===================== Entity -> DTO ===================== */
    public static OrderTableDTO toTableDto(OrderEntity entity) {
        if (entity == null) return null;

        return new OrderTableDTO(
                entity.getId(),
                entity.getThirdParty().getFullName(),
                entity.getTotal(),
                entity.getStatus(),
                entity.getObservations(),
                entity.getOrderCode(),
                entity.getSoldCode(),
                entity.getCreatedAt(),
                entity.getExpirateAt(),
                entity.getSoldAt(),
                entity.isActive(),
                entity.isSold(),
                entity.getType()
        );
    }

    /* ===================== DTO -> Entity ===================== */
    public static OrderEntity toEntity(OrderDTO dto) {
        if (dto == null) return null;

        OrderEntity entity = new OrderEntity();
        entity.setId(dto.id());
        entity.setThirdParty(ThirdPartyMapper.toEntity(dto.thirdParty()));
        entity.setTotal(dto.total());
        entity.setStatus(dto.status());
        entity.setObservations(dto.observations());
        entity.setOrderCode(dto.orderCode());
        entity.setSoldCode(dto.soldCode());
        entity.setCreatedAt(dto.createdAt());
        entity.setExpirateAt(dto.expirateAt());
        entity.setSoldAt(dto.soldAt());
        entity.setCreatedBy(dto.createdBy());
        entity.setSoldBy(dto.soldBy());
        entity.setActive(dto.isActive());
        entity.setSold(dto.isSold());
        entity.setType(dto.type());
        
        List<OrderItemEntity> items = new ArrayList<>();
        if (dto.items() != null) {
            dto.items().forEach(itemDto -> {
                OrderItemEntity item = toItemEntity(itemDto);
                item.setOrder(entity);
                items.add(item);
            });
        }
        entity.setItems(items);

        return entity;
    }

    public static OrderItemEntity toItemEntity(OrderItemDTO dto) {
        if (dto == null) return null;
        OrderItemEntity entity = new OrderItemEntity();
        entity.setId(dto.id());
        entity.setInventory(PrescriptionInventoryMapper.toEntity(dto.inventory()));
        entity.setPriceUnit(dto.priceUnit());
        entity.setUnits(dto.units());
        entity.setPriceTotal(dto.priceTotal());
        return entity;
    }
}
