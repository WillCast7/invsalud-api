package com.aurealab.model.cashRegister.repository;

import com.aurealab.model.cashRegister.entity.CashMovementEntity;
import com.aurealab.model.cashRegister.entity.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
    public List<ProductEntity> findAllByType(String type);

    @EntityGraph(attributePaths = {"name", "type", "description", "category"})
    Page<ProductEntity> findAll(Specification<ProductEntity> spec, Pageable pageable);

    List<ProductEntity> id(Long id);

    @Query("SELECT p FROM ProductEntity p WHERE p.category.id = :id")
    Set<ProductEntity> findAllByCategoryId(Long id);
}
