package com.aurealab.model.aurea.repository;

import com.aurealab.model.aurea.entity.CompanyConfigEntity;
import com.aurealab.model.aurea.entity.CompanyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyConfigRepository extends JpaRepository<CompanyConfigEntity, Long> {
    CompanyConfigEntity findByCompanyId(Long companyId);
}
