package com.aurealab.model.inventory.repository;

import com.aurealab.model.inventory.entity.PaymentMethodEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentMethodRepository extends JpaRepository<PaymentMethodEntity, Long> {
}
