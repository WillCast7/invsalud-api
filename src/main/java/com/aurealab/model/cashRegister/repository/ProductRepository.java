package com.aurealab.model.cashRegister.repository;

import com.aurealab.model.cashRegister.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
}
