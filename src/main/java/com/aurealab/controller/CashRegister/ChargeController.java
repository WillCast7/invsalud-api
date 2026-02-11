package com.aurealab.controller.CashRegister;

import com.aurealab.dto.APIResponseDTO;
import com.aurealab.dto.CashRegister.ChargeDTO;
import com.aurealab.dto.CashRegister.ThirdPartyDTO;
import com.aurealab.service.CashRegister.ChargeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/charge")
public class ChargeController {

    @Autowired
    ChargeService chargeService;

    @GetMapping(produces = "application/json", value = "/{customerId}")
    ResponseEntity<APIResponseDTO<ChargeDTO>> getChargeByCustomerId(@PathVariable Long customerId){
        return chargeService.findByCustomerId(customerId);
    }
}
