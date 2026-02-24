package com.aurealab.controller.CashRegister;

import com.aurealab.dto.APIResponseDTO;
import com.aurealab.dto.CashRegister.response.CashMovementResponseDTO;
import com.aurealab.dto.CashRegister.response.CashSessionSummaryDTO;
import com.aurealab.service.CashRegister.CashMovementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cash-movement")
public class CashMovementController {

    @Autowired
    CashMovementService cashMovementService;

    @GetMapping(produces = "application/json", value = "/{id}")
    public ResponseEntity<APIResponseDTO<CashMovementResponseDTO>> findMovementDetails(@PathVariable Long id){
        return cashMovementService.findCashMovementById(id);
    }

}
