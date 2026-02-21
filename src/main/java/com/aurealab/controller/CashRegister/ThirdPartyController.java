package com.aurealab.controller.CashRegister;

import com.aurealab.dto.APIResponseDTO;
import com.aurealab.dto.CashRegister.ThirdPartyDTO;
import com.aurealab.dto.CashRegister.request.ThirdPartyRequestDTO;
import com.aurealab.service.CashRegister.ThirdPartyService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/thirdparty")
public class ThirdPartyController {

    @Autowired
    ThirdPartyService thirdPartyService;

    @GetMapping(produces = "application/json", value = "/{id}")
    ResponseEntity<APIResponseDTO<Set<ThirdPartyDTO>>> findCustomersByDocumentNumber(@PathVariable String id){
        return thirdPartyService.findCustomersByDocumentNumber(id);
    }

    @PostMapping(produces = "application/json")
    ResponseEntity<APIResponseDTO<ThirdPartyDTO>> saveThirdParty(@RequestBody @Valid ThirdPartyRequestDTO thirdParty){
            System.out.println("thirdParty");
        System.out.println(thirdParty);
        return thirdPartyService.saveCustomer(thirdParty);
    }

}
