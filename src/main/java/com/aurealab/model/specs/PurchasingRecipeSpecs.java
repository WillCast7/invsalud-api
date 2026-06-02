package com.aurealab.model.specs;

import com.aurealab.model.inventory.entity.PurchasingRecipeEntity;
import com.aurealab.model.inventory.entity.PurchasingEntity;
import com.aurealab.model.inventory.entity.ThirdPartyEntity;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PurchasingRecipeSpecs {
    public static Specification<PurchasingRecipeEntity> reportSearch(
            LocalDateTime start,
            LocalDateTime end,
            String documentNumber,
            String product
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            Join<PurchasingRecipeEntity, PurchasingEntity> purchasingJoin = root.join("purchasing");

            if (start != null) {
                predicates.add(cb.greaterThanOrEqualTo(purchasingJoin.get("createdAt"), start));
            }
            if (end != null) {
                predicates.add(cb.lessThanOrEqualTo(purchasingJoin.get("createdAt"), end));
            }

            if (documentNumber != null && !documentNumber.trim().isEmpty()) {
                String pattern = "%" + documentNumber.toLowerCase() + "%";
                Join<PurchasingEntity, ThirdPartyEntity> tpJoin = purchasingJoin.join("thirdParty");
                predicates.add(cb.or(
                        cb.like(cb.lower(purchasingJoin.get("purchasedCode")), pattern),
                        cb.like(cb.lower(tpJoin.get("documentNumber")), pattern),
                        cb.like(cb.lower(tpJoin.get("fullName")), pattern)
                ));
            }

            if (product != null && !product.trim().isEmpty()) {
                String pattern = "%" + product.toLowerCase() + "%";
                predicates.add(cb.like(cb.literal("recetarios"), pattern));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
