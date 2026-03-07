package com.aurealab.model.cashRegister.specs;

import com.aurealab.model.cashRegister.entity.CashMovementEntity;
import com.aurealab.model.cashRegister.entity.ProductEntity;
import org.springframework.data.jpa.domain.Specification;

public class ProductSpecs {

    public static Specification<ProductEntity> search(String searchTerm) {
        return (root, query, cb) -> {
            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                return cb.conjunction();
            }

            String pattern = "%" + searchTerm.toLowerCase() + "%";

            return cb.or(
                    cb.like(cb.lower(root.get("name")), pattern),
                    cb.like(cb.lower(root.get("type")), pattern),
                    cb.like(cb.lower(root.get("description")), pattern)
            );
        };
    }


}
