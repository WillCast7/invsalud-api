package com.aurealab.dto;

import java.time.LocalDateTime;
import java.util.Map;

public record AuditLogDTO(
        Long id,
        String tableName,
        String recordId,
        String actionType,
        LocalDateTime actionTimestamp,
        String performedBy,
        Map<String, Object> oldValues,
        Map<String, Object> newValues
) {
}
