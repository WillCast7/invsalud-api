package com.aurealab.model.cashRegister.specs;

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

    public static Specification<CashMovementEntity> searchFilteredMovements(
            String searchTerm, Long thirdPartyId, String type, Long advisorId, String startDate, String endDate) {
        return (root, query, cb) -> {
            java.util.List<jakarta.persistence.criteria.Predicate> predicates = new java.util.ArrayList<>();

            if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                String pattern = "%" + searchTerm.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("customer").get("fullName")), pattern),
                        cb.like(cb.lower(root.get("customer").get("documentNumber")), pattern)
                ));
            }

            if (thirdPartyId != null) {
                predicates.add(cb.equal(root.get("customer").get("id"), thirdPartyId));
            }

            if (type != null && !type.trim().isEmpty()) {
                predicates.add(cb.equal(root.get("type"), type));
            }

            if (advisorId != null) {
                predicates.add(cb.equal(root.get("advisor").get("id"), advisorId));
            }

            if (startDate != null && endDate != null && !startDate.trim().isEmpty() && !endDate.trim().isEmpty()) {
                try {
                    java.time.LocalDate start = java.time.LocalDate.parse(startDate.split("T")[0]);
                    java.time.LocalDate end = java.time.LocalDate.parse(endDate.split("T")[0]);
                    
                    java.time.OffsetDateTime startOfDay = start.atStartOfDay().atOffset(java.time.OffsetDateTime.now().getOffset());
                    java.time.OffsetDateTime endOfDay = end.plusDays(1).atStartOfDay().atOffset(java.time.OffsetDateTime.now().getOffset()).minusNanos(1);
                    
                    predicates.add(cb.between(root.get("createdAt"), startOfDay, endOfDay));
                } catch (Exception e) {
                    // Ignore date parsing exception
                }
            }

            return predicates.isEmpty() ? cb.conjunction() : cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };
    }
}
