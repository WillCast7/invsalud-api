package com.aurealab.controller.CashRegister;

import com.aurealab.dto.APIResponseDTO;
import com.aurealab.dto.CashRegister.CashSessionDTO;
import com.aurealab.dto.LoginRequest;
import com.aurealab.service.CashRegister.CashSessionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cash-session")
public class CashSessionController {

    @Autowired
    CashSessionService cashSessionService;

    @PostMapping(value = "/initialize")
    public ResponseEntity<APIResponseDTO<CashSessionDTO>> initializeCashSession(@RequestBody CashSessionDTO cashSessionDTO){
        return cashSessionService.initializeCashSession(cashSessionDTO);
    }

    @PostMapping(value = "/finalize")
    public ResponseEntity<APIResponseDTO<CashSessionDTO>> finalizeCashSession(@RequestBody CashSessionDTO cashSessionDTO){
        return cashSessionService.finalizeCashSession(cashSessionDTO);
    }

}
