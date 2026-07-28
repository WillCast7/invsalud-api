package com.aurealab.model.aurea.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "audit_logs")
public class AuditLogEntity {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(name = "table_name", nullable = false, length = 100)
        private String tableName;

        @Column(name = "record_id", nullable = false, length = 100)
        private String recordId;

        @Column(name = "action_type", nullable = false, length = 20)
        private String actionType;

        @CreationTimestamp
        @Column(name = "action_timestamp", updatable = false)
        private LocalDateTime actionTimestamp;

        @Column(name = "performed_by", nullable = false, length = 100)
        private String performedBy;

        @Column(name = "old_values", columnDefinition = "jsonb")
        private String oldValues;

        @Column(name = "new_values", columnDefinition = "jsonb")
        private String newValues;
}
