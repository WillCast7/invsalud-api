package com.aurealab.controller;

import com.aurealab.service.DownloadReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/report")
public class DownloadReportController {

    @Autowired
    DownloadReportService pdfReportService;

    @GetMapping
    public ResponseEntity<InputStreamResource> downloadReports(
            @RequestParam(required = true) String type,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String documentNumber,
            @RequestParam(required = false) String product,
            @RequestParam(required = false) String batch,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String units){
        return pdfReportService.downloadReport(type, category, startDate, endDate, documentNumber, product, batch, status, units);
    }

    @GetMapping(value = "/order/{id}")
    public ResponseEntity<InputStreamResource> downloadOrder(@PathVariable Long id){
        return pdfReportService.downloadOrder(id);
    }

    @GetMapping(value = "/sale/{id}")
    public ResponseEntity<InputStreamResource> downloadSale(@PathVariable Long id){
        return pdfReportService.downloadSale(id);
    }

    @GetMapping(value = "/purchase/{id}")
    public ResponseEntity<InputStreamResource> downloadPurchase(@PathVariable Long id){
        return pdfReportService.downloadPurchase(id);
    }

    @GetMapping(value = "/invoice/{id}")
    public ResponseEntity<InputStreamResource> downloadInvoice(@PathVariable Long id){
        return pdfReportService.downloadInvoice(id);
    }

    
}
