package com.aurealab.model.inventory.repository;

import com.aurealab.model.inventory.entity.ResolutionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResolutionRepository extends JpaRepository<ResolutionEntity, Long> {

    @EntityGraph(attributePaths = {"thirdParty"})
    Page<ResolutionEntity> findAll(Specification<ResolutionEntity> spec, Pageable pageable);
}
