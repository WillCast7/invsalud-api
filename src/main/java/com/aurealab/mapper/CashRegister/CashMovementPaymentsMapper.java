package com.aurealab.mapper.CashRegister;

import com.aurealab.dto.CashRegister.CashMovementItemDTO;
import com.aurealab.dto.CashRegister.CashMovementPaymentDTO;
import com.aurealab.model.cashRegister.entity.CashMovementEntity;
import com.aurealab.model.cashRegister.entity.CashMovementItemsEntity;
import com.aurealab.model.cashRegister.entity.CashMovementPaymentEntity;
import com.aurealab.model.cashRegister.entity.PaymentMethodEntity;

public class CashMovementPaymentsMapper {

    /* ===================== DTO -> Entity ===================== */
    public static CashMovementPaymentEntity toEntity(CashMovementPaymentDTO dto){
        CashMovementPaymentEntity entity = new CashMovementPaymentEntity();

        entity.setAmount(dto.amount());
        if (dto.paymentMethod() != null) {
            PaymentMethodEntity methodEntity = new PaymentMethodEntity();
            methodEntity.setId(dto.paymentMethod().id());
            entity.setPaymentMethod(methodEntity);
            entity.setTransactionReference(dto.transactionReference());
        }

        return entity;
    }

    /* ===================== Entity -> DTO ===================== */
    public static CashMovementPaymentDTO toDto(CashMovementPaymentEntity entity){
        if (entity == null) return null;
        return new CashMovementPaymentDTO(
                entity.getId(),
                PaymentMethodMapper.toDto(entity.getPaymentMethod()),
                entity.getAmount(),
                entity.getTransactionReference()
        );
    }
}
