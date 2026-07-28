package com.aurealab.model.specs;

import com.aurealab.model.aurea.entity.PersonEntity;
import com.aurealab.model.aurea.entity.UserEntity;
import com.aurealab.model.inventory.entity.BatchEntity;
import com.aurealab.model.inventory.entity.PrescriptionInventoryEntity;
import com.aurealab.model.inventory.entity.ProductEntity;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Subquery;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PrescriptionInventorySpecs {

    public static Specification<PrescriptionInventoryEntity> search(String searchTerm, String type) {
        return (root, query, cb) -> {
            Predicate typePredicate = null;
            if ("public".equalsIgnoreCase(type)) {
                typePredicate = cb.isTrue(root.join("product").get("isPublicHealth"));
            } else if ("special".equalsIgnoreCase(type)) {
                typePredicate = cb.isFalse(root.join("product").get("isPublicHealth"));
            }

            Predicate activePredicate = cb.isTrue(root.get("isActive"));

            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                return typePredicate != null ? cb.and(typePredicate, activePredicate) : activePredicate;
            }
            String pattern = "%" + searchTerm.toLowerCase() + "%";

            Join<BatchEntity, PrescriptionInventoryEntity> batchJoin = root.join("batch");
            Join<ProductEntity, PrescriptionInventoryEntity> productJoin = root.join("product");

            Predicate batchPredicate = cb.like(cb.lower(batchJoin.get("code")), pattern);
            Predicate nameProductPredicate = cb.like(cb.lower(productJoin.get("name")), pattern);
            Predicate codeProductPredicate = cb.like(cb.lower(productJoin.get("code")), pattern);
            Predicate concentrationProductPredicate = cb.like(cb.lower(productJoin.get("concentration")), pattern);
            Predicate presentationProductPredicate = cb.like(cb.lower(productJoin.get("presentation")), pattern);
            Predicate pharmaceuticalFormProductPredicate = cb.like(cb.lower(productJoin.get("pharmaceuticalForm")), pattern);


            Predicate searchPredicate = cb.or(
                    batchPredicate,
                    nameProductPredicate,
                    codeProductPredicate,
                    concentrationProductPredicate,
                    presentationProductPredicate,
                    pharmaceuticalFormProductPredicate
            );

            if ("public".equalsIgnoreCase(type)) {
                return cb.and(searchPredicate, cb.isTrue(productJoin.get("isPublicHealth")), activePredicate);
            } else if ("special".equalsIgnoreCase(type)) {
                return cb.and(searchPredicate, cb.isFalse(productJoin.get("isPublicHealth")), activePredicate);
            }

            return cb.and(searchPredicate, activePredicate);

        };
    }

    public static Specification<PrescriptionInventoryEntity> searchExpired(String searchTerm, String type) {
        return (root, query, cb) -> {
            Predicate typePredicate = null;

            if ("expired".equalsIgnoreCase(type)) {
                // Combinamos ambas condiciones con un AND
                typePredicate = cb.and(
                        cb.lessThan(root.get("expirationDate"), LocalDate.now()),
                        cb.isFalse(root.get("isDrawal"))
                );
            } else if ("removed".equalsIgnoreCase(type)) {
                typePredicate = cb.isTrue(root.get("isDrawal"));
            }

            // Si no hay término de búsqueda, retornamos solo el filtro de tipo (o todo si es null)
            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                return typePredicate != null ? typePredicate : cb.conjunction();
            }

            String pattern = "%" + searchTerm.toLowerCase() + "%";

            // Joins para las búsquedas
            Join<PrescriptionInventoryEntity, BatchEntity> batchJoin = root.join("batch");
            Join<PrescriptionInventoryEntity, ProductEntity> productJoin = root.join("product");

            Predicate searchPredicate = cb.or(
                    cb.like(cb.lower(batchJoin.get("code")), pattern),
                    cb.like(cb.lower(productJoin.get("name")), pattern),
                    cb.like(cb.lower(productJoin.get("code")), pattern),
                    cb.like(cb.lower(productJoin.get("concentration")), pattern),
                    cb.like(cb.lower(productJoin.get("presentation")), pattern),
                    cb.like(cb.lower(productJoin.get("pharmaceuticalForm")), pattern)
            );

            // Retornamos la combinación de la búsqueda con el filtro de tipo
            return typePredicate != null ? cb.and(searchPredicate, typePredicate) : searchPredicate;
        };
    }

    public static Specification<PrescriptionInventoryEntity> searchInventoryReport(
            String status, String units, String product, String batch, String documentNumber) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 1. Status Filter
            if (status != null && !status.trim().isEmpty()) {
                if ("vigente".equalsIgnoreCase(status)) {
                    predicates.add(cb.isTrue(root.get("isActive")));
                    predicates.add(cb.isFalse(root.get("isDrawal")));
                    predicates.add(cb.or(
                            cb.isNull(root.get("expirationDate")),
                            cb.greaterThanOrEqualTo(root.get("expirationDate"), LocalDate.now())
                    ));
                } else if ("vencido".equalsIgnoreCase(status)) {
                    predicates.add(cb.isFalse(root.get("isDrawal")));
                    predicates.add(cb.lessThan(root.get("expirationDate"), LocalDate.now()));
                } else if ("retirado".equalsIgnoreCase(status)) {
                    predicates.add(cb.isTrue(root.get("isDrawal")));
                }
            }

            // 2. Units Filter
            if (units != null && !units.trim().isEmpty()) {
                if ("available".equalsIgnoreCase(units)) {
                    predicates.add(cb.greaterThan(root.get("availableUnits"), 0));
                } else if ("unavailable".equalsIgnoreCase(units)) {
                    predicates.add(cb.lessThanOrEqualTo(root.get("availableUnits"), 0));
                } else if ("some_but_not_available".equalsIgnoreCase(units)) {
                    predicates.add(cb.greaterThan(root.get("totalUnits"), 0));
                    predicates.add(cb.lessThanOrEqualTo(root.get("availableUnits"), 0));
                }
            }

            // 3. Product Filter
            if (product != null && !product.trim().isEmpty()) {
                Join<PrescriptionInventoryEntity, ProductEntity> productJoin = root.join("product");
                String pattern = "%" + product.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(productJoin.get("name")), pattern),
                        cb.like(cb.lower(productJoin.get("code")), pattern)
                ));
            }

            // 4. Batch Filter
            if (batch != null && !batch.trim().isEmpty()) {
                Join<PrescriptionInventoryEntity, BatchEntity> batchJoin = root.join("batch");
                String pattern = "%" + batch.toLowerCase() + "%";
                predicates.add(cb.like(cb.lower(batchJoin.get("code")), pattern));
            }

            // 5. Third Party (Tercero) Filter
            if (documentNumber != null && !documentNumber.trim().isEmpty()) {
                Subquery<Integer> subquery = query.subquery(Integer.class);
                Root<com.aurealab.model.inventory.entity.PurchasingItemEntity> subRoot = subquery.from(com.aurealab.model.inventory.entity.PurchasingItemEntity.class);
                subquery.select(cb.literal(1));
                subquery.where(
                        cb.equal(subRoot.get("inventory"), root),
                        cb.equal(subRoot.join("purchasing").join("thirdParty").get("documentNumber"), documentNumber)
                );
                predicates.add(cb.exists(subquery));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
