package com.aurealab.model.specs;

import com.aurealab.model.inventory.entity.PurchasingEntity;
import com.aurealab.model.inventory.entity.ThirdPartyEntity;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

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
}
