package com.aurealab.mapper.inventory;

import com.aurealab.dto.CashRegister.ProductDTO;
import com.aurealab.dto.ResolutionDTO;
import com.aurealab.dto.tables.ResolutionTableDTO;
import com.aurealab.model.inventory.entity.ProductEntity;
import com.aurealab.model.inventory.entity.ResolutionAllowedProductEntity;
import com.aurealab.model.inventory.entity.ResolutionEntity;

import java.util.HashSet;
import java.util.Set;

public class ResolutionMapper {
    private ResolutionMapper() {
        // Constructor privado para evitar instanciación
    }

    /* ===================== Entity -> DTO ===================== */
    public static ResolutionDTO toDto(ResolutionEntity entity) {
        if (entity == null) return null;

        Set<ProductDTO> products = new HashSet<>();
        if (entity.getAllowedProduct() != null) {
            entity.getAllowedProduct().forEach(item -> products.add(ProductMapper.toDto(item.getProduct())));
        }

        Set<com.aurealab.dto.CashRegister.ThirdPartyDTO> thirdParties = new HashSet<>();
        if (entity.getThirdParties() != null) {
            entity.getThirdParties().forEach(tp -> thirdParties.add(ThirdPartyMapper.toDtoList(tp)));
        }

        return new ResolutionDTO(
                entity.getId(),
                thirdParties,
                null,
                entity.getCode(),
                entity.getStartDate(),
                entity.getExpirationDate(),
                entity.getDescription(),
                entity.getIsActive(),
                entity.getCreatedAt(),
                entity.getCreatedBy(),
                products,
                entity.getNumberResolution()
        );
    }

    /* ===================== Entity -> DTO ===================== */
    public static ResolutionTableDTO toTableDto(ResolutionEntity entity) {
        if (entity == null) return null;

        String thirdPartyFormatted = null;
        if (entity.getThirdParties() != null && !entity.getThirdParties().isEmpty()) {
            thirdPartyFormatted = entity.getThirdParties().stream()
                    .map(tp -> tp.getFullName() != null ? tp.getFullName() : "")
                    .filter(name -> !name.isEmpty())
                    .collect(java.util.stream.Collectors.joining(", "));
        }

        return new ResolutionTableDTO(
                entity.getId(),
                thirdPartyFormatted,
                entity.getCode(),
                entity.getNumberResolution(),
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
        
        Set<com.aurealab.model.inventory.entity.ThirdPartyEntity> thirdParties = new HashSet<>();
        if (dto.thirdParties() != null) {
            dto.thirdParties().forEach(tpDto -> thirdParties.add(ThirdPartyMapper.toEntity(tpDto)));
        } else if (dto.thirdParty() != null) {
            thirdParties.add(ThirdPartyMapper.toEntity(dto.thirdParty()));
        }
        entity.setThirdParties(thirdParties);

        entity.setCode(dto.code());
        entity.setStartDate(dto.startDate());
        entity.setExpirationDate(dto.expirationDate());
        entity.setDescription(dto.description());
        entity.setIsActive(dto.isActive());
        entity.setCreatedAt(dto.createdAt());
        entity.setCreatedBy(dto.createdBy());
        entity.setNumberResolution(dto.numberResolution());
        
        if (dto.products() != null && !dto.products().isEmpty()) {
            Set<ResolutionAllowedProductEntity> items = new HashSet<>();
            dto.products().forEach(item -> {
                ResolutionAllowedProductEntity raProduct = new ResolutionAllowedProductEntity();
                raProduct.setResolution(entity);
                raProduct.setProduct(new ProductEntity(item.id())); // referencia por ID, evita entidad detached
                items.add(raProduct);
            });
            entity.setAllowedProduct(items);
        }
        return entity;
    }
}
