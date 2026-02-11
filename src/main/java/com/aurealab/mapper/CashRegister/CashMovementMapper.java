package com.aurealab.mapper.CashRegister;

import com.aurealab.dto.CashRegister.request.CashMovementRequestDTO;
import com.aurealab.dto.CashRegister.response.CashMovementResponseDTO;
import com.aurealab.model.cashRegister.entity.*;


public class CashMovementMapper {
    private CashMovementMapper() {
    }

    /* ===================== Entity -> DTO ===================== */
    public static CashMovementResponseDTO toDto(CashMovementEntity entity) {
        if (entity == null) return null;
        return new CashMovementResponseDTO(
                entity.getId(),
                entity.getCashSession() != null ? CashSessionMapper.toDto(entity.getCashSession()) : null,
                entity.getCharge() != null ? ChargeMapper.toDto(entity.getCharge()) : null,
                entity.getCustomer() != null ? ThirdPartyMapper.toDto(entity.getCustomer()) : null,
                entity.getType(),
                entity.getExpectedAmount(),
                entity.getReceivedAmount(),
                entity.getConcept(),
                entity.getProduct(),
                PaymentMethodMapper.toDto(entity.getPaymentMethod()),
                entity.getAdvisor(),
                entity.isVoid(),
                entity.getCreatedAt(),
                entity.getCreatedBySystemUserId()
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
                entity.getProduct(),
                PaymentMethodMapper.toDto(entity.getPaymentMethod()),
                entity.getAdvisor(),
                entity.isVoid(),
                entity.getCreatedAt(),
                entity.getCreatedBySystemUserId()
        );
    }


    /* ===================== DTO -> Entity ===================== */
    public static CashMovementEntity toEntity(
            CashMovementRequestDTO dto,
            Long chargeId,
            Long customerId,
            String type
    ) {
        if (dto == null) return null;

        CashMovementEntity entity = new CashMovementEntity();
        entity.setCashSession(new CashSessionEntity(dto.cashSessionId()));
        entity.setCharge(new ChargeEntity(chargeId));
        entity.setCustomer(new ThirdPartyEntity(customerId));
        entity.setType(type);
        entity.setReceivedAmount(dto.receivedAmount());
        entity.setExpectedAmount(dto.expectedAmount());
        entity.setConcept(dto.concept());
        entity.setProduct(dto.product());
        entity.setPaymentMethod(new PaymentMethodEntity(dto.paymentMethodId()));
        entity.setAdvisor(dto.advisorId());
        entity.setVoid(false);

        System.out.println("entity - ");
        System.out.println(entity);

        return entity;
    }
}
