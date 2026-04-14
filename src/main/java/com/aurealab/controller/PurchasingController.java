package com.aurealab.controller;

import com.aurealab.dto.APIResponseDTO;
import com.aurealab.dto.CashRegister.request.PurchasingRequestDTO;
import com.aurealab.dto.OrderDTO;
import com.aurealab.dto.PurchasingDTO;
import com.aurealab.service.Inventory.PurchasingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/purchasing")
public class PurchasingController {

    @Autowired
    PurchasingService purchasingService;

    @GetMapping(produces = "application/json", value = "/search/{type}")
    public ResponseEntity<APIResponseDTO<String>> getPurchasingForTable(@PathVariable String type,
                                                                        @RequestParam(defaultValue = "1") int page,
                                                                   @RequestParam(defaultValue = "10") int size,
                                                                   @RequestParam(defaultValue = "") String searchValue) {
        return purchasingService.getPurchasing(page, size, searchValue, type);
    }

    @GetMapping(produces = "application/json", value = "/{id}")
    public ResponseEntity<APIResponseDTO<PurchasingDTO>> getPurchasingById(@PathVariable Long id) {
        return purchasingService.getPurchasingById(id);
    }

    @PostMapping(produces = "application/json")
    public ResponseEntity<APIResponseDTO<String>> savePurchasing(@RequestBody PurchasingRequestDTO purchasingDTO) {
        return purchasingService.savePurchasing(purchasingDTO);
    }
}
