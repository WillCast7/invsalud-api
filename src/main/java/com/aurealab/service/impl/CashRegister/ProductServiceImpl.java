package com.aurealab.service.impl.CashRegister;

import com.aurealab.dto.CashRegister.ProductDTO;
import com.aurealab.mapper.CashRegister.ProductMapper;
import com.aurealab.model.cashRegister.entity.ProductEntity;
import com.aurealab.model.cashRegister.repository.ProductRepository;
import com.aurealab.service.CashRegister.ProductService;
import com.aurealab.service.impl.shared.TenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    ProductRepository productRepository;

    @Autowired
    TenantService tenantService;

    private String tenancy = "conduvalle";

    public List<ProductEntity> findAllProducts(){
        return tenantService.executeInTenant(tenancy, () -> {
            return productRepository.findAll();
        });
    };

    public List<ProductDTO> findProductsByType(String type){
        List<ProductDTO> productDTOS = new ArrayList<>();
        tenantService.executeInTenant(tenancy, () -> {
            return productRepository.findAllByType(type);
        }).forEach(productEntity -> productDTOS.add(ProductMapper.toDto(productEntity)));

        return productDTOS;
    };
}
