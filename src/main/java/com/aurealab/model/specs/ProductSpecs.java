package com.aurealab.model.specs;

import com.aurealab.model.inventory.entity.ProductEntity;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class ProductSpecs {

    public static Specification<ProductEntity> search(String searchTerm) {
        return search(searchTerm, null);
    }

    public static Specification<ProductEntity> search(String searchTerm, Boolean isPublicHealth) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                String pattern = "%" + searchTerm.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("name")), pattern),
                        cb.like(cb.lower(root.get("concentration")), pattern),
                        cb.like(cb.lower(root.get("presentation")), pattern),
                        cb.like(cb.lower(root.get("details")), pattern),
                        cb.like(cb.lower(root.get("pharmaceuticalForm")), pattern)
                ));
            }

            if (isPublicHealth != null) {
                predicates.add(cb.equal(root.get("isPublicHealth"), isPublicHealth));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
