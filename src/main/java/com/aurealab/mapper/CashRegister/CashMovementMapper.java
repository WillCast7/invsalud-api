package com.aurealab.mapper.CashRegister;

import com.aurealab.dto.CashRegister.CashMovementItemDTO;
import com.aurealab.dto.CashRegister.CashMovementPaymentDTO;
import com.aurealab.dto.CashRegister.request.CashMovementRequestDTO;
import com.aurealab.dto.CashRegister.response.CashMovementResponseDTO;
import com.aurealab.dto.tables.CashMovementTableDTO;
import com.aurealab.model.cashRegister.entity.*;
import com.aurealab.util.constants;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;


public class CashMovementMapper {
    private CashMovementMapper() {
    }

    /* ===================== Entity -> DTO ===================== */
    public static CashMovementResponseDTO toDto(CashMovementEntity entity) {
        if (entity == null) return null;
        Set<CashMovementItemDTO> items = new HashSet<>();
        Set<CashMovementPaymentDTO> payments = new HashSet<>();

        entity.getItems().forEach((item) -> items.add(CashMovementItemMapper.toDto(item)));
        entity.getPayments().forEach((payment) -> payments.add(CashMovementPaymentsMapper.toDto(payment)));
        return new CashMovementResponseDTO(
                entity.getId(),
                CashSessionMapper.toDto(entity.getCashSession()),
                ChargeMapper.toDto(entity.getCharge()),
                ThirdPartyMapper.toDto(entity.getCustomer()),
                entity.getType(),
                entity.getExpectedAmount(),
                entity.getReceivedAmount(),
                entity.getConcept(),
                ThirdPartyMapper.toDto(entity.getAdvisor()),
                entity.isVoid(),
                entity.getCreatedAt(),
                entity.getCreatedBySystemUserId(),
                entity.getReferenceNumber(),
                entity.getObservations(),
                items,
                payments,
                entity.isFollowingIsActive(),
                FollowingMapper.toDto(entity.getFollowing())
        );
    }

    /* ===================== Entity -> DTO ===================== */
    public static CashMovementResponseDTO toDtoItems(CashMovementEntity entity) {
        if (entity == null) return null;
        Set<CashMovementItemDTO> items = new HashSet<>();

        entity.getItems().forEach((item) -> items.add(CashMovementItemMapper.toDto(item)));

        return new CashMovementResponseDTO(
                entity.getId(),
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                entity.isVoid(),
                null,
                null,
                entity.getReferenceNumber(),
                null,
                items,
                null,
                entity.isFollowingIsActive(),
                null
        );
    }

    /* ===================== Entity -> DTO ===================== */
    public static CashMovementResponseDTO toDtoFollowing(CashMovementEntity entity) {
        if (entity == null) return null;

        return new CashMovementResponseDTO(
                entity.getId(),
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                entity.isVoid(),
                null,
                null,
                entity.getReferenceNumber(),
                null,
                null,
                null,
                entity.isFollowingIsActive(),
                FollowingMapper.toDto(entity.getFollowing())

        );
    }

    /* ===================== Entity -> DTO ===================== */
    public static CashMovementResponseDTO toDtoList(CashMovementEntity entity) {
        System.out.println("Entra al tdoList");
        if (entity == null) return null;
        return new CashMovementResponseDTO(
                entity.getId(),
                null,
                null,
                ThirdPartyMapper.toDtoList(entity.getCustomer()) ,
                entity.getType(),
                entity.getExpectedAmount(),
                entity.getReceivedAmount(),
                entity.getConcept(),
                ThirdPartyMapper.toDto(entity.getAdvisor()),
                entity.isVoid(),
                entity.getCreatedAt(),
                entity.getCreatedBySystemUserId(),
                entity.getReferenceNumber(),
                entity.getObservations(),
                null,
                null,
                entity.isFollowingIsActive(),
                null
        );
    }
    /* ===================== Entity -> TableDTO ===================== */
    public static CashMovementTableDTO toDtoTable(CashMovementEntity entity) {

        if (entity == null) return null;
        return new CashMovementTableDTO(
                entity.getId(),
                ThirdPartyMapper.toDtoList(entity.getCustomer()).fullName() ,
                Objects.equals(entity.getType(), constants.configParam.incomeTransaction) ?
                constants.configParam.incomeTransactionVar : constants.configParam.expenseTransactionVar,
                entity.getExpectedAmount(),
                entity.getReceivedAmount(),
                entity.getConcept(),
                entity.isVoid(),
                entity.getReferenceNumber(),
                Objects.equals(entity.getType(), constants.configParam.incomeTransaction) ?
                        constants.colors.success : constants.colors.danger
        );
    }


    /* ===================== DTO -> Entity ===================== */
    public static CashMovementEntity toEntity(
            CashMovementRequestDTO dto,
            Long chargeId,
            Long customerId,
            String type,
            Long userId
    ) {
        if (dto == null) return null;

        Set<CashMovementItemsEntity> items = new HashSet<>();
        if (dto.product() != null) {
            dto.product().forEach(prod -> {
                CashMovementItemsEntity itemEntity = new CashMovementItemsEntity();
                ProductEntity productEntity = new ProductEntity();

                productEntity.setId(prod.id());
                itemEntity.setProduct(productEntity);
                itemEntity.setQuantity(prod.quantity() != null ? prod.quantity() : 1);
                itemEntity.setUnitPrice(prod.basePrice());
                itemEntity.setStatus("PENDIENTE");
                System.out.println("itemEntity");
                System.out.println(itemEntity);

                items.add(itemEntity);
            });
        }


        Set<CashMovementPaymentEntity> payments = new HashSet<>();
        dto.payments().forEach(payment -> payments.add(CashMovementPaymentsMapper.toEntity(payment)));
        CashMovementEntity entity = new CashMovementEntity();
        entity.setCashSession(new CashSessionEntity(dto.cashSessionId()));
        entity.setCharge(new ChargeEntity(chargeId));
        entity.setCustomer(new ThirdPartyEntity(customerId));
        entity.setType(type);
        entity.setReceivedAmount(dto.receivedAmount());
        entity.setExpectedAmount(dto.expectedAmount());
        entity.setConcept(dto.concept());
        entity.setAdvisor(ThirdPartyMapper.toEntity(dto.advisor()));
        entity.setVoid(false);
        entity.setReferenceNumber(dto.referenceNumber());
        entity.setObservations(dto.observations());
        items.forEach(item -> item.setCashMovement(entity));
        entity.setItems(items);
        entity.setFollowingIsActive(dto.followingIsActive());
        if (dto.followingIsActive() && dto.following() != null) {
            FollowingEntity followingEntity = FollowingMapper.toEntity(dto.following(), dto.following().id() == null ? userId : null);
            if (followingEntity != null) {
                followingEntity.setCashMovement(entity);
                entity.setFollowing(followingEntity);
            }
        }
        payments.forEach(payment -> payment.setCashMovement(entity));
        entity.setPayments(payments);

        return entity;
    }
}
