package com.aurealab.model.cashRegister.repository;

import com.aurealab.model.cashRegister.entity.ChargeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChargeRepository extends JpaRepository<ChargeEntity, Long> {
}
