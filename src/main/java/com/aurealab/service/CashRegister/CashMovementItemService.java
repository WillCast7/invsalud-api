package com.aurealab.service.CashRegister;

import com.aurealab.dto.APIResponseDTO;
import org.springframework.http.ResponseEntity;

public interface CashMovementItemService {
    ResponseEntity<APIResponseDTO<String>> updateStatus(Long id, String status);
}
