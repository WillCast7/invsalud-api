package com.aurealab.service;

import com.aurealab.dto.APIResponseDTO;
import org.springframework.http.ResponseEntity;

public interface ReportService {
    ResponseEntity<APIResponseDTO<String>> getReport(
            int page, int size, String type, String category,
            String startDate, String endDate, String documentNumber,
            String product, String batch);
}
