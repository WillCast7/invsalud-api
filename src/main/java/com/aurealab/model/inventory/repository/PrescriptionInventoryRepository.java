package com.aurealab.model.inventory.repository;

import com.aurealab.model.inventory.entity.PrescriptionInventoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.Optional;
import java.time.LocalDate;
import java.util.Set;

public interface PrescriptionInventoryRepository extends JpaRepository<PrescriptionInventoryEntity, Long> {

    @EntityGraph(attributePaths = {"batch", "product"})
    Page<PrescriptionInventoryEntity> findAll(Specification<PrescriptionInventoryEntity> spec, Pageable pageable);

    Optional<PrescriptionInventoryEntity> findByBatchIdAndProductIdAndExpirationDate(Long batchId, Long productId, LocalDate expirationDate);

    @EntityGraph(attributePaths = {"batch", "product"})
    @Query("SELECT pis FROM PrescriptionInventoryEntity pis " +
            "JOIN pis.product p " +
            "JOIN pis.batch b " +
            "JOIN ResolutionAllowedProductEntity rap ON rap.product = p " +
            "JOIN rap.resolution r " +
            "WHERE r.thirdParty.id = :thirdPartyId")
    Set<PrescriptionInventoryEntity> findByThirdPartyIdGranteed(@Param("thirdPartyId") Long thirdPartyId);
}
