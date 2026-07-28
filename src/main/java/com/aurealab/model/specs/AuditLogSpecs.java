package com.aurealab.model.specs;

import com.aurealab.model.aurea.entity.AuditLogEntity;
import org.springframework.data.jpa.domain.Specification;

public class AuditLogSpecs {
    public static Specification<AuditLogEntity> search(String searchTerm) {
        return (root, query, cb) -> {
            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                return cb.conjunction();
            }
            String pattern = "%" + searchTerm.toLowerCase() + "%";

            return cb.or(
                    cb.like(cb.lower(root.get("tableName")), pattern),
                    cb.like(cb.lower(root.get("recordId")), pattern),
                    cb.like(cb.lower(root.get("actionType")), pattern),
                    cb.like(cb.lower(root.get("performedBy")), pattern)
            );
        };
    }
}
