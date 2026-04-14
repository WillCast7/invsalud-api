package com.aurealab.model.inventory.repository;

import com.aurealab.model.inventory.entity.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

    @EntityGraph(attributePaths = {"name"})
    Page<ProductEntity> findAll(Specification<ProductEntity> spec, Pageable pageable);

    List<ProductEntity> id(Long id);
}
