package com.aurealab.model.aurea.repository;

import com.aurealab.model.aurea.entity.SoftwareValidationEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.ListPagingAndSortingRepository;

import java.util.Set;

public interface SoftwareValidationRepository extends CrudRepository<SoftwareValidationEntity, Long>, ListPagingAndSortingRepository<SoftwareValidationEntity, Long> {
    @Override
    public Set<SoftwareValidationEntity> findAll();
}
