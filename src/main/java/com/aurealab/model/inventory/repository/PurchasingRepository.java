package com.aurealab.model.inventory.repository;

import com.aurealab.model.inventory.entity.PurchasingEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface PurchasingRepository extends JpaRepository<PurchasingEntity, Long>, JpaSpecificationExecutor<PurchasingEntity> {

    @EntityGraph(attributePaths = {"thirdParty", "items", "items.product", "items.batch"})
    Optional<PurchasingEntity> findById(Long id);

    @EntityGraph(attributePaths = {"thirdParty"})
    Page<PurchasingEntity> findAll(Specification<PurchasingEntity> spec, Pageable pageable);

}
