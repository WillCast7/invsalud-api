package com.aurealab.service.CashRegister;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;


public interface PdfReportService {
    public ResponseEntity<InputStreamResource> downloadReport(Long sessionId);
    public ResponseEntity<InputStreamResource> downloadInvoice(Long movementId);

}
