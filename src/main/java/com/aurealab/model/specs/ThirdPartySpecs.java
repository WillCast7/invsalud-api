package com.aurealab.model.specs;

import com.aurealab.model.cashRegister.entity.ProductEntity;
import com.aurealab.model.cashRegister.entity.ThirdPartyEntity;
import org.springframework.data.jpa.domain.Specification;

public class ThirdPartySpecs {

    public static Specification<ThirdPartyEntity> search(String searchTerm) {
        return (root, query, cb) -> {
            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                return cb.conjunction();
            }

            String pattern = "%" + searchTerm.toLowerCase() + "%";

            return cb.or(
                    cb.like(cb.lower(root.get("documentNumber")), pattern),
                    cb.like(cb.lower(root.get("fullName")), pattern),
                    cb.like(cb.lower(root.get("phoneNumber")), pattern),
                    cb.like(cb.lower(root.get("email")), pattern)
            );
        };
    }
}
