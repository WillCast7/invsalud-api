package com.aurealab.model.management.repository;

import com.aurealab.model.management.entity.ConfigParamsEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface ConfigParamsRepository extends JpaRepository<ConfigParamsEntity, Long> {
    Set<ConfigParamsEntity> findByParent(String name);
    Page<ConfigParamsEntity> findAll(Specification<ConfigParamsEntity> spec, Pageable pageable);

}
