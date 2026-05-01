package com.aurealab.model.cashRegister.repository;

import com.aurealab.model.cashRegister.entity.ProductCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductCategoryRepository extends JpaRepository<ProductCategoryEntity, Long> {
}
