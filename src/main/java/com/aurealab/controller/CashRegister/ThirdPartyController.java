package com.aurealab.controller.CashRegister;

import com.aurealab.dto.APIResponseDTO;
import com.aurealab.dto.CashRegister.ThirdPartyDTO;
import com.aurealab.service.CashRegister.ThirdPartyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/thirdparty")
public class ThirdPartyController {

    @Autowired
    ThirdPartyService thirdPartyService;

    @GetMapping(produces = "application/json", value = "/{id}")
    ResponseEntity<APIResponseDTO<Set<ThirdPartyDTO>>> getIncomeFormParams(@PathVariable String id){
        return thirdPartyService.findCustomers(id);
    }
}
