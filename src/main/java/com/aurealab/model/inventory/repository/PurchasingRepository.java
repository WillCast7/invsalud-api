package com.aurealab.model.inventory.repository;

import com.aurealab.model.inventory.entity.PurchasingEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface PurchasingRepository extends JpaRepository<PurchasingEntity, Long>, JpaSpecificationExecutor<PurchasingEntity> {

    @EntityGraph(attributePaths = {"thirdParty", "items", "items.product", "items.batch"})
    Optional<PurchasingEntity> findById(Long id);

    @EntityGraph(attributePaths = {"thirdParty"})
    Page<PurchasingEntity> findAll(Specification<PurchasingEntity> spec, Pageable pageable);

    @Query("SELECT COALESCE(SUM(p.total), 0) FROM PurchasingEntity p WHERE p.isActive = true AND p.type = :type")
    BigDecimal sumTotalByType(@Param("type") String type);

    @Query("SELECT p FROM PurchasingEntity p WHERE p.isActive = true")
    List<PurchasingEntity> findAllActivePurchases();

    @Query("SELECT COALESCE(SUM(p.total), 0) FROM PurchasingEntity p WHERE p.isActive = true AND p.createdAt BETWEEN :start AND :end")
    BigDecimal sumPurchasesTotalByDate(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COALESCE(SUM(p.total), 0) FROM PurchasingEntity p WHERE p.isActive = true AND p.type = :type AND p.createdAt BETWEEN :start AND :end")
    BigDecimal sumTotalByTypeAndDate(@Param("type") String type, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT p FROM PurchasingEntity p WHERE p.isActive = true AND p.thirdParty.id = :thirdPartyId AND p.createdAt BETWEEN :start AND :end")
    List<PurchasingEntity> findPurchasesByThirdPartyAndDateRange(@Param("thirdPartyId") Long thirdPartyId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT DISTINCT p FROM PurchasingEntity p JOIN p.items item WHERE p.isActive = true AND p.thirdParty.id = :thirdPartyId AND item.product.id = :productId AND p.createdAt BETWEEN :start AND :end")
    List<PurchasingEntity> findPurchasesByThirdPartyAndProductAndDateRange(@Param("thirdPartyId") Long thirdPartyId, @Param("productId") Long productId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}

