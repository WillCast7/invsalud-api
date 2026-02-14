package com.aurealab.model.cashRegister.repository;

import com.aurealab.dto.CashRegister.response.CashSessionSummaryDTO;
import com.aurealab.model.aurea.interfaz.CashSessionSummaryProjection;
import com.aurealab.model.cashRegister.entity.CashMovementEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Set;

public interface CashMovementRepository extends JpaRepository<CashMovementEntity, Long> {

    @EntityGraph(attributePaths = {"cashSession", "customer", "paymentMethod", "charge"})
    Page<CashMovementEntity> findAll(Specification<CashMovementEntity> spec, Pageable pageable);

    @EntityGraph(attributePaths = {"cashSession", "customer", "paymentMethod", "charge"})
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
            COALESCE(SUM(CASE WHEN cm.type = 'INCOME' THEN cm.receivedAmount ELSE 0 END), 0) AS totalIncome,
            COALESCE(SUM(CASE WHEN cm.type = 'EXPENSE' THEN cm.receivedAmount ELSE 0 END), 0) AS totalExpense,
            COALESCE(SUM(
                CASE 
                    WHEN cm.type = 'INCOME' THEN cm.receivedAmount 
                    WHEN cm.type = 'EXPENSE' THEN -cm.receivedAmount 
                    ELSE 0 
                END
            ), 0) AS netBalance,
            
            COALESCE(SUM(
                CASE 
                    WHEN cm.paymentMethod.id = 1 THEN
                        CASE 
                            WHEN cm.type = 'INCOME' THEN cm.receivedAmount 
                            WHEN cm.type = 'EXPENSE' THEN -cm.receivedAmount 
                            ELSE 0 
                        END
                    ELSE 0 
                END
            ), 0) AS netCashBalance
            
        FROM CashMovementEntity cm
        WHERE cm.cashSession.id = :sessionId
        AND cm.isVoid = false
        """)
    CashSessionSummaryProjection getSessionSummary(@Param("sessionId") Long sessionId);

    Set<CashMovementEntity> findAllByCashSessionId(@Param("sessionId") Long sessionId);
}
