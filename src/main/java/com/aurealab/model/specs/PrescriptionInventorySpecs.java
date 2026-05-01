package com.aurealab.model.specs;

import com.aurealab.model.aurea.entity.PersonEntity;
import com.aurealab.model.aurea.entity.UserEntity;
import com.aurealab.model.inventory.entity.BatchEntity;
import com.aurealab.model.inventory.entity.PrescriptionInventoryEntity;
import com.aurealab.model.inventory.entity.ProductEntity;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class PrescriptionInventorySpecs {

    public static Specification<PrescriptionInventoryEntity> search(String searchTerm, String type) {
        return (root, query, cb) -> {
            Predicate typePredicate = null;
            if ("public".equalsIgnoreCase(type)) {
                typePredicate = cb.isTrue(root.join("product").get("isPublicHealth"));
            } else if ("special".equalsIgnoreCase(type)) {
                typePredicate = cb.isFalse(root.join("product").get("isPublicHealth"));
            }

            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                return typePredicate != null ? typePredicate : cb.conjunction();
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
                return cb.and(searchPredicate, cb.isTrue(productJoin.get("isPublicHealth")));
            } else if ("special".equalsIgnoreCase(type)) {
                return cb.and(searchPredicate, cb.isFalse(productJoin.get("isPublicHealth")));
            }

            return searchPredicate;

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
}
