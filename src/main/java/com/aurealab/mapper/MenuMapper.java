package com.aurealab.mapper;

import com.aurealab.dto.MenuDTO;
import com.aurealab.model.aurea.entity.MenuItemEntity;
import org.springframework.stereotype.Component;


public class MenuMapper {
    private MenuMapper() {
    }

    /* ===================== Entity -> DTO ===================== */
    public static MenuDTO toDto(MenuItemEntity entity) {
        if (entity == null) return null;

        return new MenuDTO(
                entity.getId(),
                entity.getName(),
                entity.getFather(),
                entity.getNameFather(),
                entity.getRoute(),
                entity.getOrderMenu(),
                entity.getIcon()
        );
    }

    /* ===================== DTO -> Entity ===================== */
    public static MenuItemEntity toEntity(MenuDTO dto) {
        if (dto == null) return null;

        MenuItemEntity entity = new MenuItemEntity();
        entity.setId(dto.id());
        entity.setName(dto.name());
        entity.setFather(dto.father());
        entity.setNameFather(dto.nameFather());
        entity.setRoute(dto.route());
        entity.setOrderMenu(dto.orderMenu());
        entity.setIcon(dto.icon());

        return entity;
    }
}
