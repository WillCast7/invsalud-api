package com.aurealab.service.impl;

import com.aurealab.dto.CompanyDTO;
import com.aurealab.mapper.CompanyMapper;
import com.aurealab.model.aurea.entity.CompanyEntity;
import com.aurealab.model.aurea.entity.MenuItemEntity;
import com.aurealab.model.aurea.repository.CompanyRepository;
import com.aurealab.model.inventory.entity.RecipeInventoryEntity;
import com.aurealab.service.CompanyService;
import com.aurealab.service.Inventory.RecipeInventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Set;

@Service
public class CompanyServiceImpl implements CompanyService {

    @Autowired
    CompanyRepository companyRepository;

    @Autowired
    RecipeInventoryService recipeInventoryService;

    public CompanyDTO getCompany(){
        BigDecimal price = BigDecimal.ZERO;
        try {
            RecipeInventoryEntity recipeInventory = recipeInventoryService.findByIdEntity();
            if (recipeInventory != null && recipeInventory.getPrice() != null) {
                price = recipeInventory.getPrice();
            }
        } catch (Exception ignored) {
        }
        return CompanyMapper.toDto(getCompanyEntity(), price);
    }

    public CompanyEntity getCompanyEntity() {
        return companyRepository.findById(1L)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Company not found"));
    }

    @Override
    public CompanyDTO updateCompany(CompanyDTO dto) {
        Long companyId = (dto.id() != null && dto.id() > 0) ? dto.id() : 1L;
        CompanyEntity entity = companyRepository.findById(companyId)
                .orElseGet(CompanyEntity::new);

        entity.setId(companyId);
        if (dto.nit() != null) entity.setNit(dto.nit());
        if (dto.name() != null) entity.setName(dto.name());
        if (dto.legalName() != null) entity.setLegalName(dto.legalName());
        if (dto.taxId() != null) entity.setTaxId(dto.taxId());
        if (dto.email() != null) entity.setEmail(dto.email());
        if (dto.phone() != null) entity.setPhone(dto.phone());
        if (dto.address() != null) entity.setAddress(dto.address());
        if (dto.country() != null) entity.setCountry(dto.country());
        if (dto.type() != null) entity.setType(dto.type());
        if (dto.city() != null) entity.setCity(dto.city());
        if (dto.website() != null) entity.setWebsite(dto.website());
        if (dto.logoUrl() != null) entity.setLogoUrl(dto.logoUrl());
        if (dto.logoOrder() != null) entity.setLogoOrder(dto.logoOrder());
        if (dto.logoSold() != null) entity.setLogoSold(dto.logoSold());
        if (dto.logoPurchasing() != null) entity.setLogoPurchasing(dto.logoPurchasing());
        if (dto.nameApp() != null) entity.setNameApp(dto.nameApp());
        if (dto.subscriptionPlan() != null) entity.setSubscriptionPlan(dto.subscriptionPlan());
        if (dto.isActive() != null) entity.setIsActive(dto.isActive());
        entity.setUseIva(dto.useIva() != null ? dto.useIva() : false);
        entity.setIva(dto.iva());
        if (dto.footer() != null) entity.setFooter(dto.footer());
        entity.setDaysLimitResolution(dto.daysLimitResolution());
        entity.setUpdatedAt(java.time.LocalDateTime.now());

        BigDecimal price = null;
        try {
            RecipeInventoryEntity recipeInventory = recipeInventoryService.findByIdEntity();
            if (dto.price() != null) {
                recipeInventory.setPrice(dto.price());
                recipeInventory = recipeInventoryService.save(recipeInventory);
            }
            if (recipeInventory != null) {
                price = recipeInventory.getPrice();
            }
        } catch (Exception ignored) {
        }

        CompanyEntity saved = companyRepository.save(entity);
        return CompanyMapper.toDto(saved, price);
    }

}
