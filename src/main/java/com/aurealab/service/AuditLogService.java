package com.aurealab.service;

import com.aurealab.dto.APIResponseDTO;
import com.aurealab.dto.AuditLogDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

public interface AuditLogService {
    ResponseEntity<APIResponseDTO<String>> findAllAuditLogs(int page, int size, String searchValue);
    ResponseEntity<APIResponseDTO<AuditLogDTO>> findAuditLogById(Long id);
    Page<AuditLogDTO> findAll(Pageable pageable, String searchValue);
    AuditLogDTO findById(Long id);
}
