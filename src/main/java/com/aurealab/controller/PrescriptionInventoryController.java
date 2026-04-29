package com.aurealab.controller;

import com.aurealab.dto.APIResponseDTO;
import com.aurealab.dto.PrescriptionInventoryDTO;
import com.aurealab.service.Inventory.PrescriptionInventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/prescription-inventory")
public class PrescriptionInventoryController {

    @Autowired
    PrescriptionInventoryService prescriptionInventoryService;

    @GetMapping(produces = "application/json")
    public ResponseEntity<APIResponseDTO<String>> getPrescriptionInventoryForTable(@RequestParam(defaultValue = "1") int page,
                                                                            @RequestParam(defaultValue = "10") int size,
                                                                            @RequestParam(defaultValue = "") String searchValue,
                                                                            @RequestParam(defaultValue = "all") String type) {
        return prescriptionInventoryService.getPrescriptionInventory(page, size, searchValue, type);
    }

    @GetMapping(produces = "application/json", value = "/expired")
    public ResponseEntity<APIResponseDTO<String>> getPrescriptionInventoryExpiredForTable(@RequestParam(defaultValue = "1") int page,
                                                                            @RequestParam(defaultValue = "10") int size,
                                                                            @RequestParam(defaultValue = "") String searchValue,
                                                                            @RequestParam(defaultValue = "all") String type) {
        return prescriptionInventoryService.getPrescriptionInventory(page, size, searchValue, type);
    }

    @GetMapping(produces = "application/json", value = "/{id}")
    public ResponseEntity<APIResponseDTO<PrescriptionInventoryDTO>> getPrescriptionInventory(@PathVariable Long id) {
        return prescriptionInventoryService.getPrescriptionInventoryById(id);
    }

}
