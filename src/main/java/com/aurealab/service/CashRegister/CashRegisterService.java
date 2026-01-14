package com.aurealab.service.CashRegister;

import com.aurealab.dto.APIResponseDTO;
import com.aurealab.model.cashRegister.entity.CashSessionEntity;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface CashRegisterService{
    public ResponseEntity<APIResponseDTO<List<CashSessionEntity>>> getAllSessions();
}
