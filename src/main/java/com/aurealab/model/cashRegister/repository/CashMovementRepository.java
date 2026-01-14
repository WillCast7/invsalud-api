package com.aurealab.model.cashRegister.repository;

import com.aurealab.model.cashRegister.entity.CashMovementEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CashMovementRepository extends JpaRepository<CashMovementEntity, Long> {
}
