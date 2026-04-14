package com.aurealab.model.inventory.repository;

import com.aurealab.model.inventory.entity.BatchEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BatchRepository extends JpaRepository<BatchEntity, Long> {

    Page<BatchEntity> findAll(Specification<BatchEntity> spec, Pageable pageable);

}
