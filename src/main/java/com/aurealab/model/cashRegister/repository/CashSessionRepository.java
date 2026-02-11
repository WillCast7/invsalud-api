package com.aurealab.model.cashRegister.repository;

import com.aurealab.model.cashRegister.entity.CashSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public interface CashSessionRepository extends JpaRepository<CashSessionEntity, Long> {
    public CashSessionEntity findByBusinessDateAndStatus(
            LocalDate businessDate,
            String status
    );

    public CashSessionEntity findByBusinessDateLessThanAndStatus(
            LocalDate businessDate,
            String status
    );
}
