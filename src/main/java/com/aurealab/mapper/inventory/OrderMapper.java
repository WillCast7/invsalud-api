package com.aurealab.mapper.inventory;

import com.aurealab.dto.OrderDTO;
import com.aurealab.dto.tables.OrderTableDTO;
import com.aurealab.model.inventory.entity.OrderEntity;

import java.util.ArrayList;

public class OrderMapper {
    private OrderMapper() {
        // Constructor privado para evitar instanciación
    }

    /* ===================== Entity -> DTO ===================== */
    public static OrderDTO toDto(OrderEntity entity) {
        if (entity == null) return null;

        return new OrderDTO(
                entity.getId(),
                entity.getThirdParty(),
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
                entity.getItems() != null ? new ArrayList<>(entity.getItems()) : new ArrayList<>()
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
        entity.setThirdParty(dto.thirdParty());
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
        entity.setItems(dto.items());

        return entity;
    }
}
