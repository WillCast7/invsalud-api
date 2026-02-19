package com.aurealab.controller.CashRegister;

import com.aurealab.service.CashRegister.PdfReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/report")
public class PdfReportController {

    @Autowired
    PdfReportService pdfReportService;

    @GetMapping(value = "/daily/{id}")
    public ResponseEntity<InputStreamResource> downloadReport(@PathVariable Long id){
        return pdfReportService.downloadReport(id);
    }

    @GetMapping(value = "/invoice/{id}")
    public ResponseEntity<InputStreamResource> downloadInvoice(@PathVariable Long id){
        return pdfReportService.downloadInvoice(id);
    }
}
