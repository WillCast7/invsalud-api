package com.aurealab.model.specs;

import com.aurealab.model.inventory.entity.BatchEntity;
import com.aurealab.model.inventory.entity.OrderEntity;
import com.aurealab.model.inventory.entity.ProductEntity;
import com.aurealab.model.inventory.entity.ThirdPartyEntity;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
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

    public static Specification<OrderEntity> reportSearch(
            Boolean isSold,
            String type,
            LocalDateTime start,
            LocalDateTime end,
            String documentNumber,
            String product,
            String batch
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (isSold != null) {
                predicates.add(cb.equal(root.get("isSold"), isSold));
            }

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
                Join<OrderEntity, ThirdPartyEntity> tpJoin = root.join("thirdParty");
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("orderCode")), pattern),
                        cb.like(cb.lower(root.get("soldCode")), pattern),
                        cb.like(cb.lower(tpJoin.get("documentNumber")), pattern),
                        cb.like(cb.lower(tpJoin.get("fullName")), pattern)
                ));
            }

            if (product != null && !product.trim().isEmpty()) {
                String pattern = "%" + product.toLowerCase() + "%";
                var itemsJoin = root.join("items");
                var inventoryJoin = itemsJoin.join("inventory", JoinType.LEFT);
                var productJoin = inventoryJoin.join("product", JoinType.LEFT);

                Predicate isRecipeMatch = cb.and(
                        cb.equal(root.get("type"), "recipe"),
                        cb.like(cb.literal("recetarios"), pattern)
                );

                Predicate isProductMatch = cb.or(
                        cb.like(cb.lower(productJoin.get("name")), pattern),
                        cb.like(cb.lower(productJoin.get("code")), pattern)
                );

                predicates.add(cb.or(isRecipeMatch, isProductMatch));
            }

            if (batch != null && !batch.trim().isEmpty()) {
                String pattern = "%" + batch.toLowerCase() + "%";
                var itemsJoin = root.join("items");
                var inventoryJoin = itemsJoin.join("inventory", JoinType.LEFT);
                var batchJoin = inventoryJoin.join("batch", JoinType.LEFT);
                predicates.add(cb.like(cb.lower(batchJoin.get("code")), pattern));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
