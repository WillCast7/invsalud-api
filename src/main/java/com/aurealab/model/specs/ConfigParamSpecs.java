package com.aurealab.model.specs;

import com.aurealab.model.aurea.entity.ConfigParamsEntity;
import com.aurealab.model.inventory.entity.BatchEntity;
import org.springframework.data.jpa.domain.Specification;

public class ConfigParamSpecs {
    public static Specification<ConfigParamsEntity> search(String searchTerm) {
        return (root, query, cb) -> {
            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                return cb.conjunction();
            }
            String pattern = "%" + searchTerm.toLowerCase() + "%";

            return cb.or(
                    cb.like(cb.lower(root.get("name")), pattern),
                    cb.like(cb.lower(root.get("shortname")), pattern),
                    cb.like(cb.lower(root.get("definition")), pattern),
                    cb.like(cb.lower(root.get("parent")), pattern)
            );

        };
    }
}
