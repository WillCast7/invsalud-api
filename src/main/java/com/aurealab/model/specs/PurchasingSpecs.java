package com.aurealab.model.specs;

import com.aurealab.model.inventory.entity.PurchasingEntity;
import com.aurealab.model.inventory.entity.ThirdPartyEntity;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PurchasingSpecs {
    public static Specification<PurchasingEntity> search(String searchTerm, String type) {
        return (root, query, cb) -> {
            // 1. Predicado para el Tipo (Siempre debe aplicar si el tipo no es nulo)
            Predicate typePredicate = cb.conjunction();
            if (type != null && !type.isEmpty()) {
                typePredicate = cb.equal(cb.lower(root.get("type")), type.toLowerCase());
            }

            // 2. Si no hay término de búsqueda, solo filtramos por tipo
            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                return typePredicate;
            }

            String pattern = "%" + searchTerm.toLowerCase() + "%";

            // Corregido el orden de los genéricos del Join
            Join<PurchasingEntity, ThirdPartyEntity> thirdPartyJoin = root.join("thirdParty");

            // Predicados de búsqueda (OR entre ellos)
            Predicate searchCriteria = cb.or(
                    cb.like(cb.lower(thirdPartyJoin.get("documentNumber")), pattern),
                    cb.like(cb.lower(thirdPartyJoin.get("fullName")), pattern),
                    cb.like(cb.lower(root.get("purchasedCode")), pattern)
            );

            // 3. Resultado final: (Criterios de búsqueda) AND (Filtro de Tipo)
            return cb.and(searchCriteria, typePredicate);
        };
    }

    public static Specification<PurchasingEntity> reportSearch(
            String type,
            LocalDateTime start,
            LocalDateTime end,
            String documentNumber,
            String product,
            String batch
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (type != null && !type.trim().isEmpty()) {
                predicates.add(cb.equal(cb.lower(root.get("type")), type.toLowerCase()));
            }

            if (start != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), start));
            }
            if (end != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), end));
            }

            if (documentNumber != null && !documentNumber.trim().isEmpty()) {
                String pattern = "%" + documentNumber.toLowerCase() + "%";
                Join<PurchasingEntity, ThirdPartyEntity> tpJoin = root.join("thirdParty");
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("purchasedCode")), pattern),
                        cb.like(cb.lower(tpJoin.get("documentNumber")), pattern),
                        cb.like(cb.lower(tpJoin.get("fullName")), pattern)
                ));
            }

            if (product != null && !product.trim().isEmpty()) {
                String pattern = "%" + product.toLowerCase() + "%";
                var itemsJoin = root.join("items");
                var productJoin = itemsJoin.join("product");
                predicates.add(cb.or(
                        cb.like(cb.lower(productJoin.get("name")), pattern),
                        cb.like(cb.lower(productJoin.get("code")), pattern)
                ));
            }

            if (batch != null && !batch.trim().isEmpty()) {
                String pattern = "%" + batch.toLowerCase() + "%";
                var itemsJoin = root.join("items");
                var batchJoin = itemsJoin.join("batch");
                predicates.add(cb.like(cb.lower(batchJoin.get("code")), pattern));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
