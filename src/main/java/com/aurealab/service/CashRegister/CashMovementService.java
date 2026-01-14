package com.aurealab.service.CashRegister;

import com.aurealab.dto.APIResponseDTO;
import com.aurealab.dto.CashRegister.CashMovementDTO;
import com.aurealab.model.cashRegister.entity.CashMovementEntity;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Set;

public interface CashMovementService {
    public ResponseEntity<APIResponseDTO<Set<CashMovementDTO>>> getAllTransactions();
}
