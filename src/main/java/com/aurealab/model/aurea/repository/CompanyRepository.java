package com.aurealab.model.aurea.repository;

import com.aurealab.model.aurea.entity.CompanyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<CompanyEntity, Long> {
}
