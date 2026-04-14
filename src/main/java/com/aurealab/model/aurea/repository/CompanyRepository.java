package com.aurealab.model.aurea.repository;

import com.aurealab.model.aurea.entity.CompanyEntity;
import com.aurealab.model.aurea.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CompanyRepository extends JpaRepository<CompanyEntity, Long> {
}
