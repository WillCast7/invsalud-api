package com.aurealab.service;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;


public interface DownloadReportService {
    public ResponseEntity<InputStreamResource> downloadReport(String type, String category, String startDate, String endDate, String documentNumber, String product, String batch);
    public ResponseEntity<InputStreamResource> downloadOrder(Long sessionId);
    public ResponseEntity<InputStreamResource> downloadInvoice(Long movementId);
    public ResponseEntity<InputStreamResource> downloadSale(Long saleId);
    public ResponseEntity<InputStreamResource> downloadPurchase(Long purchaseId);
}
