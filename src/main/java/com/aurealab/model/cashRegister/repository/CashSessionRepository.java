package com.aurealab.model.cashRegister.repository;

import com.aurealab.model.cashRegister.entity.CashSessionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDate;
import java.util.List;

public interface CashSessionRepository extends JpaRepository<CashSessionEntity, Long>, JpaSpecificationExecutor<CashSessionEntity> {
    public CashSessionEntity findByBusinessDateAndStatus(
            LocalDate businessDate,
            String status
    );

    public CashSessionEntity findByBusinessDateLessThanAndStatus(
            LocalDate businessDate,
            String status
    );


    Page<CashSessionEntity> findAll(Specification<CashSessionEntity> spec, Pageable pageable);

}
