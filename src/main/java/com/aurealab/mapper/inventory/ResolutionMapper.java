package com.aurealab.mapper.inventory;

import com.aurealab.dto.CashRegister.ProductDTO;
import com.aurealab.dto.ResolutionDTO;
import com.aurealab.dto.tables.ResolutionTableDTO;
import com.aurealab.model.inventory.entity.ProductEntity;
import com.aurealab.model.inventory.entity.ResolutionEntity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ResolutionMapper {
    private ResolutionMapper() {
        // Constructor privado para evitar instanciación
    }

    /* ===================== Entity -> DTO ===================== */
    public static ResolutionDTO toDto(ResolutionEntity entity) {
        if (entity == null) return null;

        Set<ProductDTO> products = new HashSet<ProductDTO>();
        //entity.getProducts().forEach(item -> products.add(ProductMapper.toDto(item)));

        return new ResolutionDTO(
                entity.getId(),
                ThirdPartyMapper.toDtoList(entity.getThirdParty()),
                entity.getCode(),
                entity.getStartDate(),
                entity.getExpirationDate(),
                entity.getDescription(),
                entity.getIsActive(),
                entity.getCreatedAt(),
                entity.getCreatedBy(),
                products
        );
    }

    /* ===================== Entity -> DTO ===================== */
    public static ResolutionTableDTO toTableDto(ResolutionEntity entity) {
        if (entity == null) return null;

        return new ResolutionTableDTO(
                entity.getId(),
                entity.getThirdParty().getFullName(),
                entity.getCode(),
                entity.getStartDate(),
                entity.getExpirationDate(),
                entity.getDescription(),
                entity.getIsActive(),
                entity.getCreatedAt(),
                entity.getCreatedBy()
        );
    }


    /* ===================== DTO -> Entity ===================== */
    public static ResolutionEntity toEntity(ResolutionDTO dto) {
        if (dto == null) return null;

        ResolutionEntity entity = new ResolutionEntity();
        entity.setId(dto.id());
        entity.setThirdParty(ThirdPartyMapper.toEntity(dto.thirdParty()));
        entity.setCode(dto.code());
        entity.setStartDate(dto.startDate());
        entity.setExpirationDate(dto.expirationDate());
        entity.setDescription(dto.description());
        entity.setIsActive(dto.isActive());
        entity.setCreatedAt(dto.createdAt());
        entity.setCreatedBy(dto.createdBy());
        
        if (dto.items() != null) {
            Set<ProductEntity> items = new HashSet<ProductEntity>();
            dto.items().forEach(item -> items.add(ProductMapper.toEntity(item)));
            entity.setProducts(items);
        }

        return entity;
    }
}
