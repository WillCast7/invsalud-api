package com.aurealab.model.inventory.repository;

import com.aurealab.model.inventory.entity.OrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<OrderEntity, Long>, JpaSpecificationExecutor<OrderEntity> {

    @EntityGraph(attributePaths = {"thirdParty", "items"})
    Optional<OrderEntity> findById(Long id);

    @EntityGraph(attributePaths = {"thirdParty"})
    Page<OrderEntity> findAll(Specification<OrderEntity> spec, Pageable pageable);
}
