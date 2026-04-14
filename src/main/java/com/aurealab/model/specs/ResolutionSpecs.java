package com.aurealab.model.specs;

import com.aurealab.model.inventory.entity.ResolutionEntity;
import com.aurealab.model.inventory.entity.ThirdPartyEntity;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public class ResolutionSpecs {
    public static Specification<ResolutionEntity> search(String searchTerm) {
        return (root, query, cb) -> {
            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                return cb.conjunction();
            }
            String pattern = "%" + searchTerm.toLowerCase() + "%";

            Join<ThirdPartyEntity, ResolutionEntity> thirdPartyJoin = root.join("thirdParty");

            Predicate documentNumberPredicate = cb.like(cb.lower(thirdPartyJoin.get("documentNumber")), pattern);
            Predicate fullNamePredicate = cb.like(cb.lower(thirdPartyJoin.get("fullName")), pattern);
            Predicate codePredicate = cb.like(cb.lower(root.get("code")), pattern);
            Predicate descriptionPredicate = cb.like(cb.lower(root.get("description")), pattern);

            return cb.or(
                    documentNumberPredicate,
                    fullNamePredicate,
                    codePredicate,
                    descriptionPredicate
            );

        };
    }
}
