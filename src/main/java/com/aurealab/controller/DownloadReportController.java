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

    @GetMapping(value = "/order/{id}")
    public ResponseEntity<InputStreamResource> downloadReport(@PathVariable Long id){
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
