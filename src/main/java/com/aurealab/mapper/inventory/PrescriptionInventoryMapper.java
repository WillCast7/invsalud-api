package com.aurealab.mapper.inventory;

import com.aurealab.dto.PrescriptionInventoryDTO;
import com.aurealab.dto.PrescriptionInventoryTableDTO;
import com.aurealab.dto.PurchasingDTO;
import com.aurealab.model.inventory.entity.PrescriptionInventoryEntity;

import java.util.ArrayList;
import java.util.List;

public class PrescriptionInventoryMapper {
    private PrescriptionInventoryMapper() {
        // Constructor privado para evitar instanciación
    }

    /* ===================== Entity -> DTO ===================== */
    public static PrescriptionInventoryDTO toDto(PrescriptionInventoryEntity entity) {
        if (entity == null) return null;

        return PrescriptionInventoryDTO.builder()
                .id(entity.getId())
                .product(ProductMapper.toDto(entity.getProduct()))
                .batch(BatchMapper.toDto(entity.getBatch()))
                .purchasePrice(entity.getPurchasePrice())
                .salePrice(entity.getSalePrice())
                .totalUnits(entity.getTotalUnits())
                .availableUnits(entity.getAvailableUnits())
                .expirationDate(entity.getExpirationDate())
                .createdBy(entity.getCreatedBy())
                .createdAt(entity.getCreatedAt())
                .isActive(entity.isActive())
                .isDrawal(entity.isDrawal())
                .withdrawalBy(entity.getWithdrawalBy())
                .withdrawnAt(entity.getWithdrawnAt())
                .withdrawalCode(entity.getWithdrawalCode())
                .withdrawalType(entity.getWithdrawalType())
                .build();
    }
    /* ===================== Entity -> DTO ===================== */
    public static PrescriptionInventoryTableDTO toTableDto(PrescriptionInventoryEntity entity) {
        if (entity == null) return null;

        return PrescriptionInventoryTableDTO.builder()
                .id(entity.getId())
                .product(entity.getProduct().getName())
                .batch(entity.getBatch().getCode())
                .presentation(entity.getProduct().getPresentation())
                .pharmaceuticalForm(entity.getProduct().getPharmaceuticalForm())
                .purchasePrice(entity.getPurchasePrice())
                .salePrice(entity.getSalePrice())
                .totalUnits((long) entity.getTotalUnits())
                .availableUnits((long) entity.getAvailableUnits())
                .expirationDate(entity.getExpirationDate())
                .isActive(entity.isActive())
                .isDrawal(entity.isDrawal())
                .withdrawalBy(entity.getWithdrawalBy())
                .withdrawnAt(entity.getWithdrawnAt())
                .withdrawalCode(entity.getWithdrawalCode())
                .withdrawalType(entity.getWithdrawalType())
                .build();
    }

    /* ===================== DTO -> Entity ===================== */
    public static PrescriptionInventoryEntity toEntity(PrescriptionInventoryDTO dto) {
        if (dto == null) return null;

        PrescriptionInventoryEntity entity = new PrescriptionInventoryEntity();
        entity.setId(dto.id());
        entity.setProduct(ProductMapper.toEntity(dto.product()));
        entity.setBatch(BatchMapper.toEntity(dto.batch()));
        entity.setPurchasePrice(dto.purchasePrice());
        entity.setSalePrice(dto.salePrice());
        entity.setTotalUnits(dto.totalUnits());
        entity.setAvailableUnits(dto.availableUnits());
        entity.setExpirationDate(dto.expirationDate());
        entity.setCreatedBy(dto.createdBy());
        entity.setCreatedAt(dto.createdAt());
        entity.setActive(dto.isActive());
        entity.setDrawal(dto.isDrawal());
        entity.setWithdrawalBy(dto.withdrawalBy());
        entity.setWithdrawnAt(dto.withdrawnAt());
        entity.setWithdrawalCode(dto.withdrawalCode());
        entity.setWithdrawalType(dto.withdrawalType());

        return entity;
    }

    /* ===================== PurchasingDTO -> PrescriptionInventoryDTO List ===================== */
    public static List<PrescriptionInventoryDTO> fromPurchasing(PurchasingDTO purchasingDTO) {
        if (purchasingDTO == null || purchasingDTO.items() == null) return new ArrayList<>();

        return purchasingDTO.items().stream().map(item -> {

            return PrescriptionInventoryDTO.builder()
                    .product(item.product())
                    .batch(item.batch())
                    .purchasePrice(item.priceUnit())
                    .salePrice(item.sellPrice())
                    .totalUnits(item.units())
                    .availableUnits(item.units())
                    .expirationDate(item.expirationDate())
                    .createdBy(String.valueOf(purchasingDTO.createdBy()))
                    .createdAt(purchasingDTO.createdAt())
                    .isActive(purchasingDTO.isActive() != null ? purchasingDTO.isActive() : true)
                    .isDrawal(false)
                    .build();
        }).collect(java.util.stream.Collectors.toList());
    }
}
