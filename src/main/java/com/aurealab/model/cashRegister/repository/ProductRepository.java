package com.aurealab.model.cashRegister.repository;

import com.aurealab.model.cashRegister.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
    public List<ProductEntity> findAllByType(String type);
}
