package com.aurealab.controller;

import com.aurealab.dto.APIResponseDTO;
import com.aurealab.dto.CashRegister.response.CashSessionsResponseDTO;
import com.aurealab.service.CashRegister.CashMovementService;
import com.aurealab.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reports")
public class ReportsController {

    @Autowired
    CashMovementService cashMovementService;

    @GetMapping(produces = "application/json", value = "/movements")
    ResponseEntity<APIResponseDTO<CashSessionsResponseDTO>> getMovementsReport(@RequestParam(defaultValue = "1") int page,
                                                                               @RequestParam(defaultValue = "10") int size,
                                                                               @RequestParam(defaultValue = "") String searchValue,
                                                                               @RequestParam(required = false) Long thirdPartyId,
                                                                               @RequestParam(required = false) String type,
                                                                               @RequestParam(required = false) Long advisorId,
                                                                               @RequestParam(required = false) String startDate,
                                                                               @RequestParam(required = false) String endDate) {
        return cashMovementService.getFilteredTransactions(page, size, searchValue, thirdPartyId, type, advisorId, startDate, endDate);
    }

}
