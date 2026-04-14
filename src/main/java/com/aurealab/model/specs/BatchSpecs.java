package com.aurealab.model.specs;

import com.aurealab.model.inventory.entity.BatchEntity;
import org.springframework.data.jpa.domain.Specification;

public class BatchSpecs {
    public static Specification<BatchEntity> search(String searchTerm) {
        return (root, query, cb) -> {
            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                return cb.conjunction();
            }
            String pattern = "%" + searchTerm.toLowerCase() + "%";

            return cb.or(
                    cb.like(cb.lower(root.get("code")), pattern)
            );

        };
    }
}
