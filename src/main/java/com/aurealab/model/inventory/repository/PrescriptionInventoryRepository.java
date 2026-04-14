package com.aurealab.model.inventory.repository;

import com.aurealab.model.inventory.entity.PrescriptionInventoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;
import java.time.LocalDate;

public interface PrescriptionInventoryRepository extends JpaRepository<PrescriptionInventoryEntity, Long> {

    @EntityGraph(attributePaths = {"batch", "product"})
    Page<PrescriptionInventoryEntity> findAll(Specification<PrescriptionInventoryEntity> spec, Pageable pageable);

    Optional<PrescriptionInventoryEntity> findByBatchIdAndProductIdAndExpirationDate(Long batchId, Long productId, LocalDate expirationDate);
}
