package com.aurealab.model.cashRegister.repository;

import com.aurealab.model.cashRegister.entity.CashMovementItemsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CashMovementProductsRepository extends JpaRepository<CashMovementItemsEntity, Long> {
}
