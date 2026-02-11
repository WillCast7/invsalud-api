package com.aurealab.model.cashRegister.repository;

import com.aurealab.model.cashRegister.entity.PaymentMethodEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentMethodRepository extends JpaRepository<PaymentMethodEntity, Long> {
}
