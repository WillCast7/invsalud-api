package com.aurealab.controller;

import com.aurealab.dto.APIResponseDTO;
import com.aurealab.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reportes")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping(produces = "application/json")
    public ResponseEntity<APIResponseDTO<String>> getReport(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String documentNumber,
            @RequestParam(required = false) String product,
            @RequestParam(required = false) String batch) {
        return reportService.getReport(page, size, type, category, startDate, endDate, documentNumber, product, batch);
    }
}
