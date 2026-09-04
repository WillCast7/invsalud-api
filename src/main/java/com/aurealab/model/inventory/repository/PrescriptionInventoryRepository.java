package com.aurealab.model.inventory.repository;

import com.aurealab.model.inventory.entity.PrescriptionInventoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.List;
import java.util.Optional;
import java.time.LocalDate;
import java.util.Set;

public interface PrescriptionInventoryRepository extends JpaRepository<PrescriptionInventoryEntity, Long>, JpaSpecificationExecutor<PrescriptionInventoryEntity> {

    @EntityGraph(attributePaths = {"batch", "product"})
    Optional<PrescriptionInventoryEntity> findById(Long id);

    @EntityGraph(attributePaths = {"batch", "product"})
    Page<PrescriptionInventoryEntity> findAll(Specification<PrescriptionInventoryEntity> spec, Pageable pageable);

    Optional<PrescriptionInventoryEntity> findByBatchIdAndProductIdAndExpirationDate(Long batchId, Long productId, LocalDate expirationDate);

    @EntityGraph(attributePaths = {"batch", "product"})
    @Query("SELECT pis FROM PrescriptionInventoryEntity pis " +
            "JOIN pis.product p " +
            "JOIN pis.batch b " +
            "JOIN ResolutionAllowedProductEntity rap ON rap.product = p " +
            "JOIN rap.resolution r " +
            "WHERE r.thirdParty.id = :thirdPartyId " +
            "AND pis.expirationDate >= :minExpirationDate " +
            "AND (r.expirationDate IS NULL OR r.expirationDate >= :minExpirationDate) " +
            "AND pis.isDrawal = false")
    Set<PrescriptionInventoryEntity> findByThirdPartyIdGranted(
            @Param("thirdPartyId") Long thirdPartyId,
            @Param("minExpirationDate") LocalDate minExpirationDate
    );

    default Set<PrescriptionInventoryEntity> findByThirdPartyIdGranted(Long thirdPartyId) {
        return findByThirdPartyIdGranted(thirdPartyId, LocalDate.now());
    }

    @EntityGraph(attributePaths = {"batch", "product"})
    @Query("SELECT pis FROM PrescriptionInventoryEntity pis " +
            "JOIN pis.product p " +
            "WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')) " +
            "AND pis.isDrawal = false")
    List<PrescriptionInventoryEntity> findByProductNameContainingIgnoreCase(@Param("name") String name);

    @EntityGraph(attributePaths = {"batch", "product"})
    @Query("SELECT pis FROM PrescriptionInventoryEntity pis " +
            "JOIN FETCH pis.product p " +
            "LEFT JOIN FETCH pis.batch b " +
            "WHERE pis.isDrawal = false AND pis.availableUnits > 0 " +
            "ORDER BY pis.expirationDate ASC")
    List<PrescriptionInventoryEntity> findAllActiveStock();
}
