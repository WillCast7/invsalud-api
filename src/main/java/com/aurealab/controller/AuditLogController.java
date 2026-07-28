package com.aurealab.controller;

import com.aurealab.dto.APIResponseDTO;
import com.aurealab.dto.AuditLogDTO;
import com.aurealab.service.AuditLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/audit-logs")
public class AuditLogController {

    @Autowired
    private AuditLogService auditLogService;

    @GetMapping(produces = "application/json")
    public ResponseEntity<APIResponseDTO<String>> findAuditLogs(@RequestParam(defaultValue = "1") int page,
                                                                @RequestParam(defaultValue = "10") int size,
                                                                @RequestParam(defaultValue = "") String searchValue) {
        return auditLogService.findAllAuditLogs(page, size, searchValue);
    }

    @GetMapping(produces = "application/json", value = "/{id}")
    public ResponseEntity<APIResponseDTO<AuditLogDTO>> findAuditLogById(@PathVariable Long id) {
        return auditLogService.findAuditLogById(id);
    }
}
