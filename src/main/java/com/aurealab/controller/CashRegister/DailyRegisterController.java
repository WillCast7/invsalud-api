package com.aurealab.controller.CashRegister;


import com.aurealab.dto.APIResponseDTO;
import com.aurealab.dto.CashRegister.CashMovementDTO;
import com.aurealab.model.cashRegister.entity.CashMovementEntity;
import com.aurealab.model.cashRegister.entity.CashSessionEntity;
import com.aurealab.service.CashRegister.CashMovementService;
import com.aurealab.service.CashRegister.CashRegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/registrodiario")
public class DailyRegisterController {

    @Autowired
    CashRegisterService cashRegisterService;

    @Autowired
    CashMovementService cashMovementService;

    @GetMapping("/conduvalle")
    ResponseEntity<APIResponseDTO<List<CashSessionEntity>>>  getDailyTenantTransactions(){
        return cashRegisterService.getAllSessions();
    }

    @GetMapping
    ResponseEntity<APIResponseDTO<Set<CashMovementDTO>>>  getDailyTransactions(){
        return cashMovementService.getAllTransactions();
    }
}











