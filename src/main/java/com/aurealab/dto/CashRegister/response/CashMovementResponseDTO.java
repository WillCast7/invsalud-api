package com.aurealab.dto.CashRegister.response;

import com.aurealab.dto.CashRegister.*;
import com.aurealab.model.cashRegister.entity.*;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

public record CashMovementResponseDTO(
        Long id,
        CashSessionDTO cashSession,
        ChargeDTO charge,
        ThirdPartyDTO customer,
        String type,
        BigDecimal expectedAmount,
        BigDecimal receivedAmount,
        String concept,
        ThirdPartyDTO advisor,
        boolean isVoid,
        OffsetDateTime createdAt,
        Long createdBySystemUserId,
        String referenceNumber,
        String observations,
        Set<CashMovementItemDTO> items,
        Set<CashMovementPaymentDTO> payments,
        boolean followingIsActive,
        FollowingDTO following

){
}
