package com.aurealab.mapper;

import com.aurealab.dto.AuditLogDTO;
import com.aurealab.model.inventory.entity.AuditLogEntity;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

public class AuditLogMapper {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private AuditLogMapper() {
    }

    public static AuditLogDTO toDTO(AuditLogEntity entity) {
        if (entity == null) return null;

        Map<String, Object> oldValues = null;
        Map<String, Object> newValues = null;

        try {
            if (entity.getOldValues() != null && !entity.getOldValues().trim().isEmpty()) {
                oldValues = objectMapper.readValue(entity.getOldValues(), new TypeReference<Map<String, Object>>() {});
            }
            if (entity.getNewValues() != null && !entity.getNewValues().trim().isEmpty()) {
                newValues = objectMapper.readValue(entity.getNewValues(), new TypeReference<Map<String, Object>>() {});
            }
        } catch (Exception e) {
            // Silently ignore or fallback
        }

        return new AuditLogDTO(
                entity.getId(),
                entity.getTableName(),
                entity.getRecordId(),
                entity.getActionType(),
                entity.getActionTimestamp(),
                entity.getPerformedBy(),
                oldValues,
                newValues,
                entity.getIpAddress()
        );
    }

    public static AuditLogEntity toEntity(AuditLogDTO dto) {
        if (dto == null) return null;
        AuditLogEntity auditLog = new AuditLogEntity();
        auditLog.setId(dto.id());
        auditLog.setTableName(dto.tableName());
        auditLog.setRecordId(dto.recordId());
        auditLog.setActionType(dto.actionType());
        auditLog.setActionTimestamp(dto.actionTimestamp());
        auditLog.setPerformedBy(dto.performedBy());

        String oldValues = null;
        String newValues = null;

        try {
            if (dto.oldValues() != null) {
                oldValues = objectMapper.writeValueAsString(dto.oldValues());
            }
            if (dto.newValues() != null) {
                newValues = objectMapper.writeValueAsString(dto.newValues());
            }
        } catch (Exception e) {
            // Silently ignore or fallback
        }

        auditLog.setOldValues(oldValues);
        auditLog.setNewValues(newValues);
        auditLog.setIpAddress(dto.ipAddress());

        return auditLog;
    }
}
