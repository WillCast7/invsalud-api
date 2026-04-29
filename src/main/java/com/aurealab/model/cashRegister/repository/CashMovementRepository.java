package com.aurealab.model.cashRegister.repository;

import com.aurealab.dto.CashRegister.response.CashSessionSummaryDTO;
import com.aurealab.model.aurea.interfaz.CashSessionSummaryProjection;
import com.aurealab.model.cashRegister.entity.CashMovementEntity;
import com.aurealab.model.cashRegister.entity.FollowingEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface CashMovementRepository extends JpaRepository<CashMovementEntity, Long> {

    @EntityGraph(attributePaths = {"cashSession", "customer", "charge"})
    Page<CashMovementEntity> findAll(Specification<CashMovementEntity> spec, Pageable pageable);

    @EntityGraph(attributePaths = {"cashSession", "customer", "charge"})
    Set<CashMovementEntity> findAll(Specification<CashMovementEntity> spec);

    @Query("""
            SELECT COALESCE(SUM(
            CASE
                WHEN cm.type = 'INCOME' THEN cm.receivedAmount
                WHEN cm.type = 'EXPENSE' THEN -cm.receivedAmount
                ELSE 0
            END
            ), 0)
            FROM CashMovementEntity cm
            WHERE cm.cashSession.id = :sessionId AND cm.isVoid = false
        """
    )
    BigDecimal getSummary(@Param("sessionId") Long sessionId);

    @Query("""
        SELECT 
            COALESCE(SUM(CASE WHEN cm.type = 'INCOME' THEN p.amount ELSE 0 END), 0) AS totalIncome,
            COALESCE(SUM(CASE WHEN cm.type = 'EXPENSE' THEN p.amount ELSE 0 END), 0) AS totalExpense,
            
            COALESCE(SUM(
                CASE 
                    WHEN cm.type = 'INCOME' THEN p.amount 
                    WHEN cm.type = 'EXPENSE' THEN -p.amount 
                    ELSE 0 
                END
            ), 0) AS netBalance,
            
            COALESCE(SUM(
                CASE 
                    WHEN p.paymentMethod.id = 1 THEN
                        CASE 
                            WHEN cm.type = 'INCOME' THEN p.amount 
                            WHEN cm.type = 'EXPENSE' THEN -p.amount 
                            ELSE 0 
                        END
                    ELSE 0 
                END
            ), 0) AS netCashBalance
            
        FROM CashMovementEntity cm
        JOIN cm.payments p
        WHERE cm.cashSession.id = :sessionId
        AND cm.isVoid = false
        """)
    CashSessionSummaryProjection getSessionSummary(@Param("sessionId") Long sessionId);

    Set<CashMovementEntity> findAllByCashSessionId(@Param("sessionId") Long sessionId);

    @Query("SELECT cm FROM CashMovementEntity cm WHERE cm.id = :id")
    Optional<CashMovementEntity> findByIdCustom(@Param("id") Long id);

    @Query("SELECT cm FROM CashMovementEntity cm WHERE cm.customer.id = :id AND cm.followingIsActive = true")
    Set<CashMovementEntity> findAllByCustomerIdWithFollowingActive(@Param("id") Long customerId);

    @Query("SELECT cm FROM CashMovementEntity cm WHERE cm.customer.id = :id AND cm.followingIsActive = false")
    Set<CashMovementEntity> findAllByCustomerIdAndFollowingIsActiveFalse(@Param("id") Long id);

    @Query("SELECT cm FROM CashMovementEntity cm WHERE cm.customer.id = :id AND cm.following.isActive = true")
    Optional<CashMovementEntity> findFollowingActiveByThirdPartyId(@Param("id") Long id);

    List<CashMovementEntity> findAllByCustomerIdOrderByCreatedAtDesc(Long customerId);
}
