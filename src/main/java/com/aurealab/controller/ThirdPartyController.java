package com.aurealab.controller;

import com.aurealab.dto.APIResponseDTO;
import com.aurealab.dto.CashRegister.ThirdPartyDTO;
import com.aurealab.dto.CashRegister.request.ThirdPartyRequestDTO;
import com.aurealab.dto.response.ThirdPartyWithParamsResponseDTO;
import com.aurealab.service.ThirdPartyService;
import com.aurealab.service.CashRegister.CashMovementService;
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

    @Autowired
    CashMovementService cashMovementService;

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

    @GetMapping(produces = "application/json", value = "/history/{id}")
    ResponseEntity<APIResponseDTO<com.aurealab.dto.response.ThirdpartyHistoryResponseDTO>> findThirdPartyHistoryById(@PathVariable Long id){
        return thirdPartyService.findThirdPartyHistoryById(id);
    }

    @PostMapping(produces = "application/json")
    ResponseEntity<APIResponseDTO<ThirdPartyDTO>> saveThirdParty(@RequestBody @Valid ThirdPartyRequestDTO thirdParty){
            System.out.println("thirdParty");
        System.out.println(thirdParty);
        return thirdPartyService.saveCustomer(thirdParty);
    }

    @PutMapping(produces = "application/json", value = "/finishcase/items")
    ResponseEntity<APIResponseDTO<String>> finishItemsCase(@RequestBody Long movementId){
        return cashMovementService.finishItemsCase(movementId);
    }

    @PutMapping(produces = "application/json", value = "/finishcase/following")
    ResponseEntity<APIResponseDTO<String>> finishFollowingCase(@RequestBody Long movementId){
        return cashMovementService.finishFollowingCase(movementId);
    }

}
