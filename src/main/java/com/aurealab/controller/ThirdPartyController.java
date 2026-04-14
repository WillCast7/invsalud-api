package com.aurealab.controller;

import com.aurealab.dto.APIResponseDTO;
import com.aurealab.dto.CashRegister.ThirdPartyDTO;
import com.aurealab.dto.CashRegister.request.ThirdPartyRequestDTO;
import com.aurealab.dto.response.ThirdPartyWithParamsResponseDTO;
import com.aurealab.service.Inventory.ThirdPartyService;
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

    @GetMapping(produces = "application/json")
    ResponseEntity<APIResponseDTO<String>> findCustomers(@RequestParam(defaultValue = "1") int page,
                                                         @RequestParam(defaultValue = "10") int size,
                                                         @RequestParam(defaultValue = "") String searchValue){
        return thirdPartyService.findAllThirdParties(page, size, searchValue);
    }

    @GetMapping(produces = "application/json", value = "/{id}")
    ResponseEntity<APIResponseDTO<Set<ThirdPartyDTO>>> findCustomersByDocumentNumber(@PathVariable String id){
        return thirdPartyService.findCustomersByDocumentNumber(id);
    }

    @GetMapping(produces = "application/json", value = "/andparams/{id}")
    ResponseEntity<APIResponseDTO<ThirdPartyWithParamsResponseDTO>> findThirdPartyAndParamsById(@PathVariable Long id){
        return thirdPartyService.findThirdPartyAndParamsById(id);
    }

    @PostMapping(produces = "application/json")
    ResponseEntity<APIResponseDTO<ThirdPartyDTO>> saveThirdParty(@RequestBody @Valid ThirdPartyRequestDTO thirdParty){
        return thirdPartyService.saveCustomer(thirdParty);
    }

}
