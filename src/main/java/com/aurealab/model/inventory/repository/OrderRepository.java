package com.aurealab.model.inventory.repository;

import com.aurealab.model.inventory.entity.OrderEntity;
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

public interface OrderRepository extends JpaRepository<OrderEntity, Long>, JpaSpecificationExecutor<OrderEntity> {

    @EntityGraph(attributePaths = {"thirdParty", "items"})
    Optional<OrderEntity> findById(Long id);

    @EntityGraph(attributePaths = {"thirdParty"})
    Page<OrderEntity> findAll(Specification<OrderEntity> spec, Pageable pageable);

    @Query("SELECT COALESCE(SUM(o.total), 0) FROM OrderEntity o WHERE o.isSold = :isSold AND o.isActive = true AND o.type = :type")
    BigDecimal sumTotalByIsSoldAndType(@Param("isSold") boolean isSold, @Param("type") String type);

    @Query("SELECT COALESCE(SUM(o.total), 0) FROM OrderEntity o WHERE o.isSold = true AND o.status = 'SOLD' AND o.isActive = true AND o.type = :type")
    BigDecimal sumTotalSalesByType(@Param("type") String type);

    @Query("SELECT o FROM OrderEntity o WHERE o.isSold = true AND o.status = 'SOLD' AND o.isActive = true")
    List<OrderEntity> findAllSales();

    @Query("SELECT COALESCE(SUM(o.total), 0) FROM OrderEntity o WHERE o.isSold = true AND o.status = 'SOLD' AND o.isActive = true AND o.soldAt BETWEEN :start AND :end")
    BigDecimal sumSalesTotalByDate(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COALESCE(SUM(o.total), 0) FROM OrderEntity o WHERE o.isSold = :isSold AND o.isActive = true AND o.type = :type AND o.createdAt BETWEEN :start AND :end")
    BigDecimal sumTotalByIsSoldAndTypeAndDate(@Param("isSold") boolean isSold, @Param("type") String type, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COALESCE(SUM(o.total), 0) FROM OrderEntity o WHERE o.isSold = true AND o.status = 'SOLD' AND o.isActive = true AND o.type = :type AND o.soldAt BETWEEN :start AND :end")
    BigDecimal sumTotalSalesByTypeAndDate(@Param("type") String type, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT o FROM OrderEntity o WHERE o.isSold = true AND o.status = 'SOLD' AND o.isActive = true AND o.soldAt BETWEEN :start AND :end")
    List<OrderEntity> findSalesByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT o FROM OrderEntity o WHERE o.isSold = true AND o.status = 'SOLD' AND o.isActive = true AND o.thirdParty.id = :thirdPartyId AND o.soldAt BETWEEN :start AND :end")
    List<OrderEntity> findSalesByThirdPartyAndDateRange(@Param("thirdPartyId") Long thirdPartyId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT DISTINCT o FROM OrderEntity o JOIN o.items item WHERE o.isSold = true AND o.status = 'SOLD' AND o.isActive = true AND item.inventory.product.id = :productId AND o.soldAt BETWEEN :start AND :end")
    List<OrderEntity> findSalesByProductAndDateRange(@Param("productId") Long productId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT DISTINCT o FROM OrderEntity o JOIN o.items item WHERE o.isSold = true AND o.status = 'SOLD' AND o.isActive = true AND o.thirdParty.id = :thirdPartyId AND item.inventory.product.id = :productId AND o.soldAt BETWEEN :start AND :end")
    List<OrderEntity> findSalesByThirdPartyAndProductAndDateRange(@Param("thirdPartyId") Long thirdPartyId, @Param("productId") Long productId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}

