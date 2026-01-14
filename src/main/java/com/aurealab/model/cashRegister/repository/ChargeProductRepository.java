package com.aurealab.model.cashRegister.repository;

import com.aurealab.model.cashRegister.entity.ChargeProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChargeProductRepository extends JpaRepository<ChargeProductEntity, Long> {
}
