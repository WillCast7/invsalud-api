package com.aurealab.service;

import com.aurealab.dto.CompanyDTO;
import com.aurealab.model.aurea.entity.CompanyEntity;

public interface CompanyService {
    public CompanyDTO getCompany();
    public CompanyEntity getCompanyEntity();
    public CompanyDTO updateCompany(CompanyDTO companyDTO);
}
