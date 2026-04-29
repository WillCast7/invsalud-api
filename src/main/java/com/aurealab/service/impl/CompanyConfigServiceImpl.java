package com.aurealab.service.impl;

import com.aurealab.dto.CompanyConfigDTO;
import com.aurealab.mapper.CompanyConfigMapper;
import com.aurealab.model.aurea.repository.CompanyConfigRepository;
import com.aurealab.service.CompanyConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CompanyConfigServiceImpl implements CompanyConfigService {

    @Autowired
    CompanyConfigRepository companyConfigRepository;

    public CompanyConfigDTO getCompanyConfigById(Long idCompany) {
         return CompanyConfigMapper.toDto(companyConfigRepository.findByCompanyId(idCompany));
    }

}
