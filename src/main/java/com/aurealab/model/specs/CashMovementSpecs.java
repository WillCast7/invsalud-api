package com.aurealab.model.specs;

import com.aurealab.model.cashRegister.entity.CashMovementEntity;
import org.springframework.data.jpa.domain.Specification;

public class CashMovementSpecs {

    public static Specification<CashMovementEntity> searchBySessionAndTerm(Long sessionId, String searchTerm) {
        return (root, query, cb) -> {
            // Filtro obligatorio: ID de la sesión
            var predicate = cb.equal(root.get("cashSession").get("id"), sessionId);

            // Si hay un término de búsqueda, agregamos los OR
            if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                String pattern = "%" + searchTerm.toLowerCase() + "%";

                var searchPredicate = cb.or(
                        cb.like(cb.lower(root.get("customer").get("fullName")), pattern),
                        cb.like(cb.lower(root.get("customer").get("documentNumber")), pattern)
                );

                return cb.and(predicate, searchPredicate);
            }

            return predicate;
        };
    }


}
