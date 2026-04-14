package com.aurealab.model.specs;

import com.aurealab.model.inventory.entity.BatchEntity;
import com.aurealab.model.inventory.entity.OrderEntity;
import com.aurealab.model.inventory.entity.ProductEntity;
import com.aurealab.model.inventory.entity.ThirdPartyEntity;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public class OrderSpecs {
    public static Specification<OrderEntity> search(String searchTerm) {
        return (root, query, cb) -> {
            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                return cb.conjunction();
            }
            String pattern = "%" + searchTerm.toLowerCase() + "%";

            Join<ThirdPartyEntity, OrderEntity> thirdPartyJoin = root.join("thirdParty");

            Predicate documentNumberPredicate = cb.like(cb.lower(thirdPartyJoin.get("documentNumber")), pattern);
            Predicate fullNamePredicate = cb.like(cb.lower(thirdPartyJoin.get("fullName")), pattern);
            Predicate orderCodePredicate = cb.like(cb.lower(root.get("orderCode")), pattern);
            Predicate soldCodePredicate = cb.like(cb.lower(root.get("soldCode")), pattern);

            return cb.or(
                    documentNumberPredicate,
                    fullNamePredicate,
                    orderCodePredicate,
                    soldCodePredicate
            );

        };
    }
}
