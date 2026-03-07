package com.aurealab.model.cashRegister.specs;

import com.aurealab.model.cashRegister.entity.CashMovementEntity;
import com.aurealab.model.cashRegister.entity.CashSessionEntity;
import com.aurealab.model.cashRegister.entity.ThirdPartyEntity;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

public class CashSessionSpecs {
    public static Specification<CashSessionEntity> searchByMovementOrThirdParty(String searchValue) {
        return (root, query, cb) -> {
            if (searchValue == null || searchValue.isBlank()) {
                return null;
            }

            String pattern = "%" + searchValue.toLowerCase() + "%";

            assert query != null;
            jakarta.persistence.criteria.Subquery<Long> subquery = query.subquery(Long.class);
            Root<CashMovementEntity> movementRoot = subquery.from(CashMovementEntity.class);
            Join<CashMovementEntity, ThirdPartyEntity> customerJoin = movementRoot.join("customer");

            subquery.select(movementRoot.get("cashSession").get("id"));

            Predicate refPredicate = cb.like(cb.lower(movementRoot.get("referenceNumber")), pattern);
            Predicate namePredicate = cb.like(cb.lower(customerJoin.get("fullName")), pattern);
            Predicate docPredicate = cb.like(cb.lower(customerJoin.get("documentNumber")), pattern);

            subquery.where(cb.or(refPredicate, namePredicate, docPredicate));

            return cb.in(root.get("id")).value(subquery);
        };
    }
}
