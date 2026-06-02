package com.aurealab.service;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;


public interface DownloadReportService {
    public ResponseEntity<InputStreamResource> downloadOrder(Long sessionId);
    public ResponseEntity<InputStreamResource> downloadInvoice(Long movementId);
    public ResponseEntity<InputStreamResource> downloadSale(Long saleId);
    public ResponseEntity<InputStreamResource> downloadPurchase(Long purchaseId);
}
