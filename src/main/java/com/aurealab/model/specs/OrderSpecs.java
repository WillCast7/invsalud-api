package com.aurealab.model.specs;

import com.aurealab.model.inventory.entity.BatchEntity;
import com.aurealab.model.inventory.entity.OrderEntity;
import com.aurealab.model.inventory.entity.ProductEntity;
import com.aurealab.model.inventory.entity.ThirdPartyEntity;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class OrderSpecs {
    public static Specification<OrderEntity> search(String searchTerm, boolean isSold, String type) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // Filtros base obligatorios
            predicates.add(cb.equal(root.get("isSold"), isSold));
            predicates.add(cb.equal(root.get("type"), type));

            // Filtro de búsqueda (si existe)
            if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                String pattern = "%" + searchTerm.toLowerCase() + "%";
                Join<OrderEntity, ThirdPartyEntity> thirdPartyJoin = root.join("thirdParty");

                Predicate documentNumberPredicate = cb.like(cb.lower(thirdPartyJoin.get("documentNumber")), pattern);
                Predicate fullNamePredicate = cb.like(cb.lower(thirdPartyJoin.get("fullName")), pattern);
                Predicate orderCodePredicate = cb.like(cb.lower(root.get("orderCode")), pattern);
                Predicate soldCodePredicate = cb.like(cb.lower(root.get("soldCode")), pattern);

                predicates.add(cb.or(
                        documentNumberPredicate,
                        fullNamePredicate,
                        orderCodePredicate,
                        soldCodePredicate
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
