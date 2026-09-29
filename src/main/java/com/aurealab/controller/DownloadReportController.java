package com.aurealab.controller;

import com.aurealab.service.DownloadReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

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
    public ResponseEntity<InputStreamResource> downloadOrder(
            @PathVariable Long id,
            @RequestParam(required = false) UUID templateId){
        return pdfReportService.downloadOrder(id, templateId);
    }

    @GetMapping(value = "/order/{id}/preview", produces = "text/html;charset=UTF-8")
    public ResponseEntity<String> previewOrder(
            @PathVariable Long id,
            @RequestParam(required = false) UUID templateId){
        return ResponseEntity.ok(pdfReportService.getOrderHtml(id, templateId));
    }

    @GetMapping(value = "/sale/{id}")
    public ResponseEntity<InputStreamResource> downloadSale(
            @PathVariable Long id,
            @RequestParam(required = false) UUID templateId){
        return pdfReportService.downloadSale(id, templateId);
    }

    @GetMapping(value = "/sale/{id}/preview", produces = "text/html;charset=UTF-8")
    public ResponseEntity<String> previewSale(
            @PathVariable Long id,
            @RequestParam(required = false) UUID templateId){
        return ResponseEntity.ok(pdfReportService.getSaleHtml(id, templateId));
    }

    @GetMapping(value = "/sale/{id}/excel")
    public ResponseEntity<InputStreamResource> downloadSaleExcel(@PathVariable Long id){
        return pdfReportService.downloadSaleExcel(id);
    }

    @GetMapping(value = "/purchase/{id}")
    public ResponseEntity<InputStreamResource> downloadPurchase(
            @PathVariable Long id,
            @RequestParam(required = false) UUID templateId){
        return pdfReportService.downloadPurchase(id, templateId);
    }

    @GetMapping(value = "/purchase/{id}/preview", produces = "text/html;charset=UTF-8")
    public ResponseEntity<String> previewPurchase(
            @PathVariable Long id,
            @RequestParam(required = false) UUID templateId){
        return ResponseEntity.ok(pdfReportService.getPurchaseHtml(id, templateId));
    }

    @GetMapping(value = "/invoice/{id}")
    public ResponseEntity<InputStreamResource> downloadInvoice(@PathVariable Long id){
        return pdfReportService.downloadInvoice(id);
    }

    
}
