package com.aurealab.service.impl;

import com.aurealab.dto.CompanyDTO;
import com.aurealab.mapper.CompanyMapper;
import com.aurealab.model.aurea.entity.CompanyEntity;
import com.aurealab.model.aurea.entity.MenuItemEntity;
import com.aurealab.model.aurea.repository.CompanyRepository;
import com.aurealab.service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class CompanyServiceImpl implements CompanyService {

    @Autowired
    CompanyRepository companyRepository;

    public CompanyDTO getCompany(){
        return CompanyMapper.toDto(getCompanyEntity());
    }

    public CompanyEntity getCompanyEntity() {
        return companyRepository.findById(1L)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Company not found"));
    }

}
