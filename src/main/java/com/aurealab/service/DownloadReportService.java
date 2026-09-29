package com.aurealab.service;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;


public interface DownloadReportService {
    public ResponseEntity<InputStreamResource> downloadReport(String type, String category, String startDate, String endDate, String documentNumber, String product, String batch, String status, String units);
    public ResponseEntity<InputStreamResource> downloadOrder(Long sessionId);
    public ResponseEntity<InputStreamResource> downloadOrder(Long sessionId, java.util.UUID templateId);
    public ResponseEntity<InputStreamResource> downloadInvoice(Long movementId);
    public ResponseEntity<InputStreamResource> downloadSale(Long saleId);
    public ResponseEntity<InputStreamResource> downloadSale(Long saleId, java.util.UUID templateId);
    public ResponseEntity<InputStreamResource> downloadPurchase(Long purchaseId);
    public ResponseEntity<InputStreamResource> downloadPurchase(Long purchaseId, java.util.UUID templateId);
    public ResponseEntity<InputStreamResource> downloadSaleExcel(Long saleId);
    public String getOrderHtml(Long orderId, java.util.UUID templateId);
    public String getSaleHtml(Long saleId, java.util.UUID templateId);
    public String getPurchaseHtml(Long purchaseId, java.util.UUID templateId);
}
